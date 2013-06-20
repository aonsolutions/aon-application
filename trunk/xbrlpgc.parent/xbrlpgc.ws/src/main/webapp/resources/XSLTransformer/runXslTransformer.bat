@echo off

@echo.
if ""%1""=="""" goto end_noParam1
if ""%2""=="""" goto end_noParam2
if ""%3""=="""" goto end_noParam3
if ""%4""=="""" goto end_noParam4
REM if ""%5""=="""" goto end_noParam5

IF EXIST "./jre1.6.0_02/bin/java.exe" GOTO relativePath
GOTO absolutPath

:relativePath
"./jre1.6.0_02/bin/java.exe" -classpath %1/xslTransformer.jar;%1/saxon9-ant.jar;%1/saxon9-dom.jar;%1/saxon9-s9api.jar;%1/saxon9.jar;%1/xercesImpl.jar;%1/xml-apis.jar;%1/log4j-1.2.15.jar;%1  es.inteco.xbrl.pgc.utils.XSLTransformer %2 %3 %4 %5

@echo.
@echo.
GOTO end

:absolutPath
java.exe -classpath %1/xslTransformer.jar;%1/saxon9-ant.jar;%1/saxon9-dom.jar;%1/saxon9-s9api.jar;%1/saxon9.jar;%1/xercesImpl.jar;%1/xml-apis.jar;%1/log4j-1.2.15.jar;%1  es.inteco.xbrl.pgc.utils.XSLTransformer %2 %3 %4 %5

@echo.
@echo.
goto end


:end_noParam1
@echo FAILED. Param 1 [base path] is required
shift
goto end


:end_noParam2
@echo FAILED. Param 2 [input document] is required
shift
goto end


:end_noParam3
@echo FAILED. Param 3 [style sheet] is required
shift
goto end


:end_noParam4
@echo FAILED. Param 4 [output document] is required
shift
goto end



REM :end_noParam5
REM @echo FAILED. Param 5 [output path] is required
REM shift
REM goto end

REM exit %ERROR_LEVEL%

:end
exit %ERROR_LEVEL%