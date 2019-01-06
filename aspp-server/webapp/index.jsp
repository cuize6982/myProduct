<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>数据传输中...</title>
    </head>

    <body>
        <div>
               <!-- escape 用于设置是否编码，若为true会将内容（如：空格会被转成&nbsp;）进行转换，建议设置成false，事实上，设置成true麻烦事更多 -->
               <!-- <s:property value="sHtmlText" escape="false"/>  -->
               ${sHtmlText}
        </div>      
    </body>
</html>