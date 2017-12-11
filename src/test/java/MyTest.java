/*
 * Copyright (c) 2005, 2016, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

import model.AuthorModel;
import model.BookModel;
import model.StudentModel;
import net.evecom.excel.ExcelContext;
import net.evecom.excel.parsing.ExcelError;
import net.evecom.excel.result.ExcelImportResult;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 小橙希 on 2017/11/30.
 * @author Iverson Cai 
 * @created 2017 /11/30 10:18:55
 */
public class MyTest {
    
    
    // 测试时文件磁盘路径
    private static String path = "test-excel111.xlsx";
    // 配置文件路径
    private static ExcelContext context = new ExcelContext("excel-config.xml");
    // Excel配置文件中配置的id
    private static String excelId = "student";
    
    @Test
    public void testTemplate() throws Exception{
        OutputStream ops = new FileOutputStream(path);
        Workbook workbook = context.createExcelTemplate(excelId,null,null);
        workbook.write(ops);
        ops.close();
        workbook.close();
    }

    @Test
    public void testExport() throws Exception {
        OutputStream ops = new FileOutputStream("test-excel5555555.xlsx");
        List<StudentModel> list = new ArrayList<StudentModel>();
        for (int i = 0; i < 1000000; i++) {
            StudentModel studentModel = new StudentModel();
            studentModel.setId("100" + i);
            studentModel.setName("name" + i);
            studentModel.setAge(i);
            studentModel.setStudentNo("stuno_" + i);
            studentModel.setCreateTime(new Date());
            studentModel.setStatus(1);

            BookModel bookModel = new BookModel();
            bookModel.setBookName("book_" + i);
            bookModel.setPrice(1.0);
            AuthorModel authorModel = new AuthorModel();
            authorModel.setAuthorName("author_" + i);
            bookModel.setAuthor(authorModel);
            studentModel.setBook(bookModel);
            list.add(studentModel);
        }
        Workbook workbook = context.createExcel(excelId, list);
        workbook.write(ops);
        ops.close();
        workbook.close();
    }

    @Test
    public void testImport() throws Exception {
        Resource resource = new ClassPathResource("test-excel333.xlsx");
        ExcelImportResult result = context.readExcel(excelId, 0, resource.getInputStream(), true);
        List<StudentModel> stus = result.getListBean();
        for (StudentModel stu : stus) {
            System.out.println(stu);
        }
        System.out.println("-----------");
        List<ExcelError> data = result.getErrors();
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i));
        }

        OutputStream ops = new FileOutputStream("test-excel444.xlsx");
        Workbook workbook = context.createErrorExcel(excelId, data, null, null);
        workbook.write(ops);
        ops.close();
        workbook.close();
    }
}
