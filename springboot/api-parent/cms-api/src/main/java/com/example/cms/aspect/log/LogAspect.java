package com.example.cms.aspect.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(apiOperation)")
    public Object invoke(ProceedingJoinPoint joinPoint, ApiOperation apiOperation) throws Throwable {


        long t1 = System.currentTimeMillis();


        String methodDetail = joinPoint.getSignature().toString();

        StringBuilder sb = new StringBuilder("\n==================访问方法开始==================\n");




        sb.append("\tAPP正在访问链接:" + request.getRequestURI()+"\n");


        sb.append("\t当前正在访问方法：" + methodDetail+"\n");


        sb.append("\t该方法注释为：" + apiOperation.value()+"\n");

        Signature signature = joinPoint.getSignature();
        MethodSignature ms = (MethodSignature) signature;

        Object[] args = joinPoint.getArgs();

        String[] parameterNames = ms.getParameterNames();

        if (parameterNames != null && parameterNames.length>0) {
            sb.append("\t括号内参数:\n");
            for (int i = 0; i < parameterNames.length; i++) {

                String paramValue = "";
                if (args[i] != null) {
                    if (!ServletRequest.class.isAssignableFrom(args[i].getClass())
                            &&!ServletResponse.class.isAssignableFrom(args[i].getClass())
                    ) {
                        paramValue = JSON.toJSONString(args[i], SerializerFeature.WriteBigDecimalAsPlain);
                    }
                }


                sb.append("\t\t参数").append(i).append(":").append(parameterNames[i]).append("=").append(paramValue).append("\n");
            }
        }


        Object retrunValue = joinPoint.proceed();
        sb.append("\t方法的返回值为：").append(JSON.toJSONString(retrunValue, SerializerFeature.WriteBigDecimalAsPlain)).append("\n");


        long t2 = System.currentTimeMillis();

        sb.append("\t耗时：").append(TimeUnit.MILLISECONDS.toMillis(t2-t1)+"毫秒\n");


        sb.append("==================访问方法结束==================\n");
        LOGGER.info(sb.toString());

        return retrunValue;
    }


}
