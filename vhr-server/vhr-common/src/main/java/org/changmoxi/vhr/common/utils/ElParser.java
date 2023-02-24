package org.changmoxi.vhr.common.utils;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.TreeMap;

/**
 * @author CZS
 * @create 2023-02-24 9:43
 **/
public class ElParser {
    /**
     * SpringEl表达式解析器
     * 不足之处: 暂不支持按照 参数下标#p0 以及 多个#参数读取，不如自带的SpringEl解析功能完善
     *
     * @param elString
     * @param map
     * @return
     */
    public static String parse(String elString, TreeMap<String, Object> map) {
        elString = String.format("#{%s}", elString);
        //创建表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        //通过evaluationContext.setVariable可以在上下文中设定变量
        EvaluationContext context = new StandardEvaluationContext();
        map.forEach(context::setVariable);

        //解析表达式，如果表达式是一个模板表达式，需要为解析传入模板解析器上下文
        Expression expression = parser.parseExpression(elString, new TemplateParserContext());
        //使用Expression.getValue()获取表达式的值，这里传入了Evaluation上下文，第二个参数是类型参数，表示返回值类型
        return expression.getValue(context, String.class);
    }
}