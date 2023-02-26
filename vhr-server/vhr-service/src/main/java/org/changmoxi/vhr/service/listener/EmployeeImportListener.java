package org.changmoxi.vhr.service.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.info.EmployeeFixedInfo;
import org.changmoxi.vhr.common.info.RegexInfo;
import org.changmoxi.vhr.common.message.basic.MailProducer;
import org.changmoxi.vhr.common.utils.SpringContextHolder;
import org.changmoxi.vhr.dto.EmployeeErrorDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
import org.changmoxi.vhr.dto.EmployeeMailDTO;
import org.changmoxi.vhr.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ImportEmployeeListener不能被Spring管理，每次读取Excel都要new出来
 *
 * @author CZS
 * @create 2023-01-26 18:16
 **/
@Data
@Slf4j
public class EmployeeImportListener extends AnalysisEventListener<EmployeeImportDTO> {
    /**
     * 每隔 BATCH_COUNT条数据 存储数据库，然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 50;
    /**
     * 缓存的数据
     */
    private List<EmployeeImportDTO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * Mapper或Service
     */
    private EmployeeService employeeService;
    /**
     * 用于民族、政治面貌、部门、职位、职称的name与id的映射
     */
    private Map<String, Map<String, Integer>> allIdMaps;
    /**
     * 导入表的表头数据
     */
    private Map<Integer, String> headMap;
    /**
     * 错误数据
     */
    private List<EmployeeErrorDTO> errorDataList;
    /**
     * 用于导出错误数据收集表
     */
    private ExcelWriter excelWriter = null;
    /**
     * 用于导出错误数据收集表
     */
    private WriteSheet writeSheet = null;
    /**
     * 错误数据收集表的表名(带有.xlsx)
     */
    private String errorDataFileName = null;
    /**
     * 员工数据导入时间
     */
    private final String CURRENT_TIME = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    /**
     * 错误数据收集表导出路径 和 其他员工固定信息
     */
    private static final EmployeeFixedInfo FIXED_INFO = SpringContextHolder.getBean(EmployeeFixedInfo.class);
    /**
     * 正则表达式
     */
    private static final RegexInfo REGEX_INFO = SpringContextHolder.getBean(RegexInfo.class);
    /**
     * 邮件消息生产者
     */
    private MailProducer mailProducer = SpringContextHolder.getBean(MailProducer.class);

    /**
     * 读取数据而不需要收集错误数据，使用这个构造器，每次创建Listener的时候需要把Spring管理的类(比如Mapper)传进来
     *
     * @param employeeService
     * @param allIdMaps
     */
    public EmployeeImportListener(EmployeeService employeeService, Map<String, Map<String, Integer>> allIdMaps) {
        this.employeeService = employeeService;
        this.allIdMaps = allIdMaps;
        this.headMap = null;
        this.errorDataList = new ArrayList<>();
    }

    /**
     * 读取数据并且需要收集错误数据，使用这个构造器，每次创建Listener的时候需要把Spring管理的类(比如Mapper)传进来
     *
     * @param employeeService
     * @param allIdMaps
     * @param errorDataFileName
     */
    public EmployeeImportListener(EmployeeService employeeService, Map<String, Map<String, Integer>> allIdMaps, String errorDataFileName) {
        this.employeeService = employeeService;
        this.allIdMaps = allIdMaps;
        this.errorDataFileName = errorDataFileName;
        this.headMap = null;
        this.errorDataList = new ArrayList<>();
    }

    /**
     * 解析数据之前调用，headMap中存放着表头数据
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(EmployeeImportDTO data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        // 数据校验
        EmployeeErrorDTO errorData = validator(data, context);
        if (Objects.nonNull(errorData)) {
            log.error("第{}行数据校验不通过，但是继续解析下一行", context.readRowHolder().getRowIndex() + 1);
            initExcelWriterAndWriteSheet();
            errorDataList.add(errorData);
            return;
        }
        // 数据处理
        data.setNationId(allIdMaps.get("nationIdMap").get(data.getNationName()));
        data.setPoliticsId(allIdMaps.get("politicsIdMap").get(data.getPoliticsStatusName()));
        data.setDepartmentId(allIdMaps.get("departmentIdMap").get(data.getDepartmentName()));
        data.setPositionId(allIdMaps.get("positionIdMap").get(data.getPositionName()));
        data.setJobLevelId(allIdMaps.get("jobLevelIdMap").get(data.getJobLevelName()));
        data.CalculateContractTerm();
        Map<Integer, Integer> departmentIdToSalaryIdMap = employeeService.getDepartmentIdToSalaryIdMap();
        if (departmentIdToSalaryIdMap.containsKey(data.getDepartmentId())) {
            data.setSalaryId(departmentIdToSalaryIdMap.get(data.getDepartmentId()));
        }
        cachedDataList.add(data);
        // 达到BATCH_COUNT，需要存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成，清理list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了就会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        // 如果有错误数据，则导出错误数据收集表
        collectErrorData();
        log.info("所有数据解析完成！");
    }

    /**
     * 存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        try {
            employeeService.saveImportEmployees(cachedDataList);
            // 存储完成，批量发送入职欢迎邮件
            List<EmployeeMailDTO> employeeMailDTOList = new ArrayList<>();
            cachedDataList.forEach(employeeImportDTO -> {
                EmployeeMailDTO employeeMailDTO = new EmployeeMailDTO();
                BeanUtils.copyProperties(employeeImportDTO, employeeMailDTO);
                employeeMailDTOList.add(employeeMailDTO);
            });
            mailProducer.sendBatchWelcomeMails(employeeMailDTOList);
        } catch (DataAccessException e) {
            // 捕获数据入库异常
            // 如果存在校验通过后仍不符合MySQL要求的数据入库，会报错，捕获异常并全局处理返回提示，同时这种情况下当前批次的数据都不会入库
            // 比如日期数据错误Incorrect date value，对应DataAccessException异常的子类DataIntegrityViolationException异常
            log.error("====================数据入库失败，原因如下====================");
            throw e;
        }
        log.info("存储数据库成功！");
    }

    /**
     * 收集错误数据
     */
    private void collectErrorData() {
        if (!CollectionUtils.isEmpty(errorDataList)) {
            log.info("{}条错误数据，开始收集！", errorDataList.size());
            excelWriter.write(errorDataList, writeSheet);
            log.info("收集完成！文件位置:{}", errorDataFileName);
        }
        // 最后要关闭流
        if (Objects.nonNull(excelWriter)) {
            excelWriter.finish();
        }
    }

    /**
     * 转换异常的数据处理和收集
     * 抛出异常则停止读取
     * 如果这里不抛出异常 则继续解析下一行
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        initExcelWriterAndWriteSheet();
        log.error("第{}行数据转换失败，但是继续解析下一行，失败原因:{}", context.readRowHolder().getRowIndex() + 1, exception.getCause().getMessage());
        // 处理和收集转换异常的数据
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException convertException = (ExcelDataConvertException) exception;
            int columnIndex = convertException.getColumnIndex();
            // 该行数据的转换异常的列的名称
            String columnName = headMap.get(columnIndex);
            // 该行数据的转换异常单元格内容
            String errorContent = convertException.getCellData().getStringValue();

            EmployeeErrorDTO errorData = new EmployeeErrorDTO();
            // 转换异常提示
            String remark = "{" + columnName + "} 转换异常 {" + errorContent + "}";
            // 设置该行数据的错误提示
            errorData.setErrorTips(context, remark);

            errorDataList.add(errorData);
        }
    }

    /**
     * 初始化错误数据收集表的 ExcelWriter 和 WriteSheet
     */
    private void initExcelWriterAndWriteSheet() {
        if (Objects.isNull(excelWriter)) {
            errorDataFileName = FIXED_INFO.getErrorDataFilePath() + CURRENT_TIME + "_" + errorDataFileName;
            excelWriter = EasyExcel.write(errorDataFileName, EmployeeErrorDTO.class).build();
        }
        if (Objects.isNull(writeSheet)) {
            writeSheet = EasyExcel.writerSheet("错误员工数据").build();
        }
    }

    /**
     * 数据校验
     *
     * @param data
     * @param context
     * @return
     */
    private EmployeeErrorDTO validator(EmployeeImportDTO data, AnalysisContext context) {
        boolean flag = true;
        EmployeeErrorDTO errorData = new EmployeeErrorDTO();
        String remark = null;

        // TODO 导入数据校验待完善
        if (StringUtils.isBlank(data.getName())) {
            errorData.setName("员工姓名{格式错误:不能为空}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getWorkId()) || data.getWorkId().length() != 8 || !StringUtils.isNumeric(data.getWorkId())) {
            errorData.setWorkId("工号{格式错误:不能为空/8位数字/前面补0}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getGender()) || !FIXED_INFO.getGenderInfo().contains(data.getGender())) {
            errorData.setGender("性别{格式错误:不能为空/男或女}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getBirthday()) || !Pattern.matches(REGEX_INFO.getDateRegex(), data.getBirthday())) {
            errorData.setBirthday("出生日期{格式错误:不能为空/格式为yyyy-MM-dd}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getIdCard()) || !Pattern.matches(REGEX_INFO.getIdCardRegex(), data.getIdCard())) {
            errorData.setIdCard("身份证号码{格式错误:18位身份证号码}");
        }
        if (StringUtils.isBlank(data.getWedlock()) || !FIXED_INFO.getWedlockInfo().contains(data.getWedlock())) {
            errorData.setWedlock("婚姻状况{格式错误:不能为空/未婚或已婚或离异}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getNationName()) || !allIdMaps.get("nationIdMap").containsKey(data.getNationName())) {
            errorData.setNationName("民族{格式错误:不能为空/56个民族}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getNativePlace())) {
            errorData.setNativePlace("籍贯{格式错误:不能为空}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getPoliticsStatusName()) || !allIdMaps.get("politicsIdMap").containsKey(data.getPoliticsStatusName())) {
            errorData.setPoliticsStatusName("政治面貌{格式错误}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getPhone()) || !Pattern.matches(REGEX_INFO.getPhoneRegex(), data.getPhone())) {
            errorData.setPhone("电话号码{格式错误:11位电话号码}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getEmail()) || !Pattern.matches(REGEX_INFO.getEmailRegex(), data.getEmail())) {
            errorData.setEmail("电子邮箱{格式错误}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getAddress())) {
            errorData.setAddress("联系地址{格式错误:不能为空}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getDepartmentName()) || !allIdMaps.get("departmentIdMap").containsKey(data.getDepartmentName())) {
            errorData.setDepartmentName("所属部门{格式错误}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getPositionName()) || !allIdMaps.get("positionIdMap").containsKey(data.getPositionName())) {
            errorData.setPositionName("职位{格式错误}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getJobLevelName()) || !allIdMaps.get("jobLevelIdMap").containsKey(data.getJobLevelName())) {
            errorData.setJobLevelName("职称{格式错误}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getHighestDegree()) || !FIXED_INFO.getHighestDegreeInfo().contains(data.getHighestDegree())) {
            errorData.setHighestDegree("最高学历{格式错误}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getSchool())) {
            errorData.setSchool("毕业院校{格式错误:不能为空}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getMajor())) {
            errorData.setMajor("所属专业{格式错误:不能为空}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getEngageForm()) || !FIXED_INFO.getEngageFormInfo().contains(data.getEngageForm())) {
            errorData.setEngageForm("聘用形式{格式错误:不能为空/劳动合同或劳务合同}");
            flag = false;
        }

        if (StringUtils.isBlank(data.getEmploymentDate()) || !Pattern.matches(REGEX_INFO.getDateRegex(), data.getEmploymentDate())) {
            errorData.setEmploymentDate("入职日期{格式错误:不能为空/格式为yyyy-MM-dd}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getConversionDate()) || !Pattern.matches(REGEX_INFO.getDateRegex(), data.getConversionDate())) {
            errorData.setConversionDate("转正日期{格式错误:不能为空/格式为yyyy-MM-dd}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getBeginContractDate()) || !Pattern.matches(REGEX_INFO.getDateRegex(), data.getBeginContractDate())) {
            errorData.setBeginContractDate("合同起始日期{格式错误:不能为空/格式为yyyy-MM-dd}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getEndContractDate()) || !Pattern.matches(REGEX_INFO.getDateRegex(), data.getEndContractDate())) {
            errorData.setEndContractDate("合同终止日期{格式错误:不能为空/格式为yyyy-MM-dd}");
            flag = false;
        }
        if (StringUtils.isBlank(data.getWorkStatus()) || !FIXED_INFO.getWorkStatusInfo().contains(data.getWorkStatus())) {
            errorData.setWorkStatus("在职状态{格式错误:不能为空/在职或离职}");
            flag = false;
        }

        // 设置错误提示数据
        errorData.setErrorTips(context, remark);
        return flag ? null : errorData;
    }
}