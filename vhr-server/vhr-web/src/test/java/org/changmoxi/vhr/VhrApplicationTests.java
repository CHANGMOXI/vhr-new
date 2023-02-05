package org.changmoxi.vhr;

import org.changmoxi.vhr.common.info.EmployeeFixedInfo;
import org.changmoxi.vhr.listener.EmployeeImportListener;
import org.changmoxi.vhr.mapper.EmployeeMapper;
import org.changmoxi.vhr.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class VhrApplicationTests {
    @Value("${error-data-excel-file-path}")
    private String PATH;
    @Resource
    private EmployeeFixedInfo fixedInfo;
    @Resource
    private EmployeeService employeeService;
    @Resource
    private EmployeeMapper employeeMapper;

    @Test
    void contextLoads() {
//        List<Employee> employees = employeeService.getEmployeesByPage(1, 10, null);
//        String fileName = PATH + "测试.xlsx";
//        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
//        EasyExcel.write(fileName, Employee.class).sheet("模板").doWrite(employees);
//        System.out.println(employees);


        String fileName = "错误员工数据导入表.xlsx";
//        String pathName = PATH + "错误员工数据导入表.xlsx";
//        EasyExcel.read(pathName, EmployeeImportDTO.class, new EmployeeImportListener(employeeMapper, employeeService.getAllIdMaps(), fileName)).sheet().doRead();

        EmployeeImportListener listener = new EmployeeImportListener(employeeMapper, employeeService.getAllIdMaps(), fileName);

        System.out.println(PATH);
        System.out.println(fixedInfo.getGenderInfo());
        System.out.println(fixedInfo.getWedlockInfo());
        System.out.println(fixedInfo.getHighestDegreeInfo());
        System.out.println(fixedInfo.getEngageFormInfo());
        System.out.println(fixedInfo.getWorkStatusInfo());

//        String DATE_REGEX = "^((?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29)$";
//        for (int i = 1; i < 10000; i++) {
//            String year = String.format("%04d", i);
//            String date = null;
//            if ((i % 4 == 0 && i % 100 != 0) || i % 400 == 0) {
//                date = year + "-02-30";
//                if (!Pattern.matches(DATE_REGEX, date)) {
//                    System.out.println("匹配失败: 闰年 " + date);
//                }
//            } else {
//                date = year + "-01-31";
//                if (!Pattern.matches(DATE_REGEX, date)) {
//                    System.out.println("匹配失败: 平年 " + date);
//                }
//            }
//        }

//        List<Object> dataList = new ArrayList<>();
//        for (int i = 0; i < 27; i++) {
//            dataList.add(null);
//        }
//        dataList.set(0, "0");
//        dataList.set(10, "10");
//        dataList.set(24, "24");
//        System.out.println(dataList);


//        long start = System.currentTimeMillis();
//
//        Object[] data = new Object[50000];
//        Arrays.fill(data, null);
//        List<Object> dataList = Arrays.stream(data).collect(Collectors.toList());
//
////        List<Object> dataList = new ArrayList<>();
////        for (int i = 0; i < 50000; i++) {
////            dataList.add(null);
////        }
//
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//        System.out.println(dataList);
    }
}