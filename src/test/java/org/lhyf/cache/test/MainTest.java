package org.lhyf.cache.test;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

/****
 * @author YF
 * @date 2018-08-04 16:59
 * @desc MainTest
 *
 **/
public class MainTest {


    @Test
    public void spelTest() {
        // 创建一个ExpressionParser对象，用于解析表达式
        ExpressionParser parser = new SpelExpressionParser();
        //------------使用SpEL调用方法-----------
        // 调用String对象的substring()方法
        System.out.println(parser.parseExpression("'VipMao'.substring(0,3)").getValue());
        List<String> list = new ArrayList<String>();
        list.add("java");
        list.add("PHP");
        // 创建一个EvaluationContext对象，作为SpEL解析变量的上下文
        EvaluationContext ctx = new StandardEvaluationContext();
        // 设置两个变量
        ctx.setVariable("list", list);
        System.out.println(parser.parseExpression("#list.subList(0,2)").getValue(ctx));
    }



}
