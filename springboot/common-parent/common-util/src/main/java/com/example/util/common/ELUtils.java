package com.example.util.common;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ELUtils {

    /**
     *
     *
                 // 准备工作
                 Person person = new Person("Tom", 18); // 一个普通的POJO
                 List<String> list = Lists.newArrayList("a", "b");
                 Map<String, String> map = Maps.newHashMap();
                 map.put("A", "1");
                 map.put("B", "2");
                 EvaluationContext context = new StandardEvaluationContext();  // 表达式的上下文,
                 context.setVariable("person", person);                        // 为了让表达式可以访问该对象, 先把对象放到上下文中
                 context.setVariable("map", map);
                 context.setVariable("list", list);
                 ExpressionParser parser = new SpelExpressionParser();
                 // 属性
                 parser.parseExpression("#person.name").getValue(context, String.class);       // Tom , 属性访问
                 parser.parseExpression("#person.Name").getValue(context, String.class);       // Tom , 属性访问, 但是首字母大写了
                 // 列表
                 parser.parseExpression("#list[0]").getValue(context, String.class)           // a , 下标
                 // map
                 parser.parseExpression("#map[A]").getValue(context, String.class);           // 1 , key
                 // 方法
                 parser.parseExpression("#person.getAge()").getValue(context, Integer.class); // 18 , 方法访问
     *
     * @param params  map.put("user",new User("1"))
     * @param expression #user.id
     * @param returnClazzType Integer.class
     * @param <T>
     * @return
     */
    public static <T> T getELValue(Map<String,Object> params, String expression,Class<T> returnClazzType) {

        StandardEvaluationContext context = new StandardEvaluationContext();

        context.setVariables(params);

        ExpressionParser parser = new SpelExpressionParser();

        return parser.parseExpression(expression).getValue(context,returnClazzType);
    }

    public static void main(String[] args) {

        List<Integer> a = new ArrayList<>();
        a.add(1);

        Map map = new HashMap();
        map.put("aa", "122");
        map.put("a", a);
        System.out.println(getELValue(map,"#aa",String.class));
        System.out.println(getELValue(map,"#a[0]",String.class));
    }

}
