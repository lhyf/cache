package org.lhyf.cache.util;

import org.lhyf.cache.exception.CacheException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;

/****
 * @author YF
 * @date 2018-08-04 17:10
 * @desc SpelEvaluator
 *
 **/
public class SpelEvaluator implements Function<Object, Object> {

    private static ExpressionParser parser;
    private static ParameterNameDiscoverer parameterNameDiscoverer;

    static {
        try {
            //since spring 4.1
            Class modeClass = Class.forName("org.springframework.expression.spel.SpelCompilerMode");

            try {
                Constructor<SpelParserConfiguration> c = SpelParserConfiguration.class
                        .getConstructor(modeClass, ClassLoader.class);
                Object mode = modeClass.getField("IMMEDIATE").get(null);
                SpelParserConfiguration config = c.newInstance(mode, SpelEvaluator.class.getClassLoader());
                parser = new SpelExpressionParser(config);
            } catch (Exception e) {
                throw new CacheException(e);
            }
        } catch (ClassNotFoundException e) {
            parser = new SpelExpressionParser();
        }
        parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    private final Expression expression;
    private String[] parameterNames;

    public SpelEvaluator(String script, Method defineMethod) {
        expression = parser.parseExpression(script);
        if (defineMethod.getParameterCount() > 0) {
            parameterNames = parameterNameDiscoverer.getParameterNames(defineMethod);
        }
    }

    @Override
    public Object apply(Object o) {
        return null;
    }

}
