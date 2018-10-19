package com.veitch.code.controller;

import com.veitch.code.build.Mybatisorm;
import com.veitch.code.vo.GeneratorVo;
import com.veitch.code.vo.ResponResult;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 分词控制器
 */
@RestController
public class CodeGeController {


    /**
     * 代码生成
     */
    @PostMapping("/generate")
    public Object codeGenerate(GeneratorVo generatorVo, HttpServletRequest request) {
        final String path = request.getSession().getServletContext().getRealPath(File.separator);
        generatorVo.setContextPath(path);
        ResponResult responResult = new ResponResult();
        try {
            responResult = Mybatisorm.generateCode(generatorVo);
        } catch (Exception e) {
            if ("数据表需要有主键".equals(e.getMessage())) {
                responResult.setRspCode("000006");
            }
            e.printStackTrace();
        }
        return responResult;
    }

}
