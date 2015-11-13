package com.efeiyi.ec.system.product.service.impl;

import com.efeiyi.ec.master.model.Master;
import com.efeiyi.ec.product.model.*;
import com.efeiyi.ec.project.model.Project;
import com.efeiyi.ec.project.model.ProjectPropertyValue;
import com.efeiyi.ec.system.product.dao.ProductDao;
import com.efeiyi.ec.system.product.model.ProductModelBean;
import com.efeiyi.ec.system.product.service.ProductManager;
import com.efeiyi.ec.tenant.model.Tenant;
import com.ming800.core.base.dao.XdoDao;
import com.ming800.core.p.service.AutoSerialManager;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/17.
 */
@Service
public class ProductManagerImpl implements ProductManager{

    @Autowired
    private ProductDao productDao;

    @Autowired
    private XdoDao xdoDao;

    @Autowired
    private AutoSerialManager autoSerialManager;

    @Override
    public int getMaxRecommendedIndex(String categoryId) {

        return productDao.getMaxRecommendedIndex(categoryId);
    }


    @Override
    public Product saveProductDescription(ProductDescription productDescription){

        Product productTemp = (Product)xdoDao.getObject(Product.class.getName(),productDescription.getProduct().getId());

        try {
            if("".equals(productDescription.getId())){
                productDescription.setId(null);
                productDescription.setProduct(productTemp);
                xdoDao.saveOrUpdateObject(ProductDescription.class.getName(), productDescription);
                productTemp.setProductDescription(productDescription);
                xdoDao.saveOrUpdateObject(Product.class.getName(), productTemp);
            }else {
                xdoDao.saveOrUpdateObject(ProductDescription.class.getName(), productDescription);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return  productTemp;
    }

    @Override
    public Product saveProduct(Product product) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if("".equals(product.getId())){
                product.setId(null);
                product.setCreateDateTime(sdf.parse(sdf.format(new Date())));
                product.setStatus("2");
            }else {
                product.setProductModelList(xdoDao.getObjectList("from ProductModel where status!='0' and product.id=?",new Object[]{product.getId()}));
            }
            System.out.print("".equals(product.getProductDescription().getId()));
            if("".equals(product.getProductDescription().getId())){
                product.setProductDescription(null);
            }else {
                product.setProductDescription((ProductDescription) xdoDao.getObject(ProductDescription.class.getName(), product.getProductDescription().getId()));
            }
            if("".equals(product.getMaster().getId())){
                product.setMaster(null);
            }else {
                product.setMaster((Master) xdoDao.getObject(Master.class.getName(), product.getMaster().getId()));
            }
            if("".equals(product.getProject().getId())){
                product.setProject(null);
            }else {
                product.setProject((Project) xdoDao.getObject(Project.class.getName(), product.getProject().getId()));
            }
            if ("".equals(product.getTenant().getId())){
                product.setTenant((Tenant) xdoDao.getObject(Tenant.class.getName(), product.getTenant().getId()));
            }


            xdoDao.saveOrUpdateObject(Product.class.getName(),product);
        }catch (Exception e){
            e.printStackTrace();
        }


        return product;
    }


    @Override
    public Product saveProductModel(ProductModelBean productModelBean) throws  Exception{

        String[] ids = productModelBean.getModelId();
        String[] status = productModelBean.getModelStatus();
        Product product = (Product)xdoDao.getObject(Product.class.getName(), productModelBean.getProductId());
        if(ids != null){
            if (ids.length > 0) {
                for (int i = 0,j=0; i < ids.length; i++) {
                    if ("".equals(ids[i])) {
                        ProductModel productModel = new ProductModel();
                        productModel.setName(productModelBean.getModelName()[i]);
                        if(!"".equals(productModelBean.getModelAmount()[i])) {
                            productModel.setAmount(Integer.parseInt(productModelBean.getModelAmount()[i]));
                        }else {
                            productModel.setAmount(null);
                        }

                        if(!"".equals(productModelBean.getModelPrice()[i])) {
                            productModel.setPrice(new BigDecimal(productModelBean.getModelPrice()[i]));
                        }else {
                            productModel.setPrice(null);
                        }
                        if(!"".equals(productModelBean.getMarketPrice()[i])) {
                            productModel.setMarketPrice(new BigDecimal(productModelBean.getMarketPrice()[i]));
                        }else {
                            productModel.setMarketPrice(null);
                        }
                        productModel.setProduct(product);
                        productModel.setSerial(autoSerialManager.nextSerial("productModel"));
                        productModel.setStatus(status[i]);
                        if("1".equals(status[i])){
                            xdoDao.saveOrUpdateObject(productModel);
                            String[] propertyValueIds = productModelBean.getModelProperty()[i].split(",");
                            for (String propertyValueId : propertyValueIds) {
                                ProjectPropertyValue projectPropertyValue = (ProjectPropertyValue) xdoDao.getObject(ProjectPropertyValue.class.getName(), propertyValueId);
                                ProductPropertyValue productPropertyValue = new ProductPropertyValue();
                                productPropertyValue.setProductModel(productModel);
                                productPropertyValue.setProjectPropertyValue(projectPropertyValue);
                                productPropertyValue.setProjectProperty(projectPropertyValue.getProjectProperty());
                                xdoDao.saveOrUpdateObject(productPropertyValue);
                            }
                        }
                        if("2".equals(status[i])){
                            productModel.setCustomProperty(productModelBean.getProperty()[j++]);
                            xdoDao.saveOrUpdateObject(productModel);
                        }

                    } else {
                        if ("0".equals(productModelBean.getModelStatus()[i])||"-1".equals(productModelBean.getModelStatus()[i])) {
                            ProductModel temp = (ProductModel)xdoDao.getObject(ProductModel.class.getName(),ids[i]);

                            xdoDao.deleteObject(ProductModel.class.getName(), ids[i]);
                            if(temp!=null) {
                                deleteProductPropertyValue(ids[i]);
                            }
                            if("-1".equals(productModelBean.getModelStatus()[i])){
                                j++;
                            }
                        } else if ("1".equals(productModelBean.getModelStatus()[i])) {
                            ProductModel productModel = (ProductModel) xdoDao.getObject(ProductModel.class.getName(), ids[i]);
                            if(!"".equals(productModelBean.getModelAmount()[i])) {
                                productModel.setAmount(Integer.parseInt(productModelBean.getModelAmount()[i]));
                            }else {
                                productModel.setAmount(null);
                            }

                            if(!"".equals(productModelBean.getModelPrice()[i])) {
                                productModel.setPrice(new BigDecimal(productModelBean.getModelPrice()[i]));
                            }else {
                                productModel.setPrice(null);
                            }
                            if(!"".equals(productModelBean.getMarketPrice()[i])) {
                                productModel.setMarketPrice(new BigDecimal(productModelBean.getMarketPrice()[i]));
                            }else {
                                productModel.setMarketPrice(null);
                            }
                         //   productModel.setAmount(Integer.parseInt(productModelBean.getModelAmount()[i]));
                            productModel.setName(productModelBean.getModelName()[i]);
                         //   productModel.setPrice(new BigDecimal(productModelBean.getModelPrice()[i]));
                            xdoDao.saveOrUpdateObject(productModel);
                            deleteProductPropertyValue(ids[i]);
                            String[] propertyValueIds = productModelBean.getModelProperty()[i].split(",");
                            for (String propertyValueId : propertyValueIds) {
                                ProjectPropertyValue projectPropertyValue = (ProjectPropertyValue) xdoDao.getObject(ProjectPropertyValue.class.getName(), propertyValueId);
                                ProductPropertyValue productPropertyValue = new ProductPropertyValue();
                                productPropertyValue.setProductModel(productModel);
                                productPropertyValue.setProjectPropertyValue(projectPropertyValue);
                                productPropertyValue.setProjectProperty(projectPropertyValue.getProjectProperty());
                                xdoDao.saveOrUpdateObject(productPropertyValue);
                            }
                        }else  if("2".equals(productModelBean.getModelStatus()[i])){
                            ProductModel productModel = (ProductModel) xdoDao.getObject(ProductModel.class.getName(), ids[i]);
                            if(!"".equals(productModelBean.getModelAmount()[i])) {
                                productModel.setAmount(Integer.parseInt(productModelBean.getModelAmount()[i]));
                            }else {
                                productModel.setAmount(null);
                            }

                            if(!"".equals(productModelBean.getModelPrice()[i])) {
                                productModel.setPrice(new BigDecimal(productModelBean.getModelPrice()[i]));
                            }else {
                                productModel.setPrice(null);
                            }
                            if(!"".equals(productModelBean.getMarketPrice()[i])) {
                                productModel.setMarketPrice(new BigDecimal(productModelBean.getMarketPrice()[i]));
                            }else {
                                productModel.setMarketPrice(null);
                            }
                    //        productModel.setAmount(Integer.parseInt(productModelBean.getModelAmount()[i]));
                            productModel.setName(productModelBean.getModelName()[i]);
                    //        productModel.setPrice(new BigDecimal(productModelBean.getModelPrice()[i]));
                            productModel.setCustomProperty(productModelBean.getProperty()[j++]);
                            xdoDao.saveOrUpdateObject(productModel);
                        }
                    }
                }
            }
        }
        if ("".equals(productModelBean.getDefaultId())) {
            ProductModel productModel = new ProductModel();
            if(!"".equals(productModelBean.getDefaultPrice())) {
                productModel.setAmount(Integer.parseInt(productModelBean.getDefaultAmount()));
            }else {
                productModel.setAmount(null);
            }
            productModel.setName(productModelBean.getDefaultName());
            if(!"".equals(productModelBean.getDefaultPrice())) {
                productModel.setPrice(new BigDecimal(productModelBean.getDefaultPrice()));
            }else {
                productModel.setPrice(null);
            }
            if(!"".equals(productModelBean.getDefaultMarketPrice())) {
                productModel.setMarketPrice(new BigDecimal(productModelBean.getDefaultMarketPrice()));
            }else {
                productModel.setMarketPrice(null);
            }
            productModel.setProduct(product);
            productModel.setSerial(autoSerialManager.nextSerial("productModel"));
            productModel.setStatus(productModelBean.getDefaultStatus());
            xdoDao.saveOrUpdateObject(productModel);
        }else {
            ProductModel productModel = (ProductModel) xdoDao.getObject(ProductModel.class.getName(), productModelBean.getDefaultId());
            if(!"".equals(productModelBean.getDefaultPrice())) {
                productModel.setAmount(Integer.parseInt(productModelBean.getDefaultAmount()));
            }
            productModel.setName(productModelBean.getDefaultName());
            if(!"".equals(productModelBean.getDefaultPrice())) {
                productModel.setPrice(new BigDecimal(productModelBean.getDefaultPrice()));
            }
            if(!"".equals(productModelBean.getDefaultMarketPrice())) {
                productModel.setMarketPrice(new BigDecimal(productModelBean.getDefaultMarketPrice()));
            }
       //     productModel.setAmount(Integer.parseInt(productModelBean.getDefaultAmount()));
            productModel.setName(productModelBean.getDefaultName());
       //     productModel.setPrice(new BigDecimal(productModelBean.getDefaultPrice()));
            xdoDao.saveOrUpdateObject(productModel);
        }

        return  product;
    }

    public void deleteProductPropertyValue(String modelId){
        List<ProductPropertyValue> pvs =  (List<ProductPropertyValue>)xdoDao.getObjectList("from ProductPropertyValue where 1=1 and productModel.id = ?",new Object[]{modelId});
        for(ProductPropertyValue productPropertyValue : pvs){
            xdoDao.deleteObject(ProductPropertyValue.class.getName(),productPropertyValue.getId());
        }
    }


    public void removeProduct(String id){

    }

    @Override
    public Subject saveSubject(Subject subject, String[] flag, String[] spId, String[] subjectPicture) {
        SubjectDescription subjectDescription = subject.getSubjectDescription();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if("".equals(subject.getId())) {
            subject.setId(null);
            try {
                subject.setCreateDateTime(sdf.parse(sdf.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            subjectDescription.setId(null);
        }
        xdoDao.saveOrUpdateObject(subjectDescription);
        subject.setSubjectDescription(subjectDescription);
        if("".equals(subject.getSubjectShow())){
            subject.setSubjectShow("0");
        }
        subject.setStatus("1");
        xdoDao.saveOrUpdateObject(subject);
            for(int i=0;i<spId.length;i++){
                if("0".equals(spId[i])){
                    SubjectPicture subjectPicture1 = new SubjectPicture();
                    subjectPicture1.setSubject(subject);
                    subjectPicture1.setPictureUrl(subjectPicture[i]);
                    xdoDao.saveOrUpdateObject(subjectPicture1);

                }else {
                    if("-1".equals(flag[i])){
                        xdoDao.deleteObject(SubjectPicture.class.getName(),spId[i]);

                    }
                }
            }

        return subject;
    }

    @Override
    public Product setProductStatus(String status, String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String identify = sdf.format(new Date());
        Product product = (Product)xdoDao.getObject(Product.class.getName(),id);
        product.setStatus(status);
        if(status.equals("1")){
            try {
                product.setShowDateTime(sdf.parse(identify));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        xdoDao.saveOrUpdateObject(product);
        return product;
    }

    /***
     * 输出表格
     */
    @Override
    public  String outExcel1(String[] homes,String on,String down){
        List<Object[]> resultList = productDao.getResult();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date1 = sdf.format(date);
//        HSSFWorkbook wb = new HSSFWorkbook();//创建一个EXCEL文件
//        HSSFSheet sheet = wb.createSheet("商品规格表");//工作簿
//        HSSFDataFormat format = wb.createDataFormat();//单元格样式
//        sheet.setColumnWidth((short)3,20*256);//单元格宽度
//        sheet.setColumnWidth((short)4, 20* 256);
//        sheet.setDefaultRowHeight((short)300);
//        HSSFCellStyle style = wb.createCellStyle(); // 样式对象
//        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
//        //style1.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
//        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);设置单元格颜色
//        style.setWrapText(true);   //设置是否能够换行，能够换行为true
//        style.setBorderBottom((short)1);   //设置下划线，参数是黑线的宽度
//        style.setBorderLeft((short)1);   //设置左边框
//        style.setBorderRight((short)1);   //设置有边框
//        style.setBorderTop((short)1);   //设置下边框
//        style.setDataFormat(format.getFormat("￥#,##0"));    //--->设置为单元格内容为货币格式
//        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));    //--->设置单元格内容为百分数格式
//        HSSFRow rowHome = sheet.createRow(0);
//
//        for(int n = 0;n<homes.length;n++){
//            rowHome.createCell(n).setCellValue(homes[n]);
//        }

        FileOutputStream fileOut = null;
        String path = this.getClass().getResource("/").getPath().toString() + "com/efeiyi/ec/system/download";
        File downloadFile = new File(path);

        if(!downloadFile.exists()){
            downloadFile.mkdir();
        }
        String fileName = path+"//productModel"+date1+".xls";
        try{
            File file = new File(fileName);
            file.createNewFile();
         //   fileName = file.getName();
            fileOut = new FileOutputStream(file);

            WritableWorkbook workbook = Workbook.createWorkbook(fileOut);//创建工作簿

            WritableSheet sheet1 = workbook.createSheet("商品规格详情",1);
            /** **********设置纵横打印（默认为纵打）、打印纸***************** */
            SheetSettings sheetset = sheet1.getSettings();
            sheetset.setProtected(false);


            /** ************设置单元格字体************** */
            WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableFont BoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);

            /** ************以下设置三种单元格样式，灵活备用************ */
            // 用于标题居中
            WritableCellFormat wcf_center = new WritableCellFormat(BoldFont);
            wcf_center.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
            wcf_center.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
            wcf_center.setAlignment(Alignment.CENTRE); // 文字水平对齐
            wcf_center.setWrap(false); // 文字是否换行

            // 用于正文居左
            WritableCellFormat wcf_left = new WritableCellFormat(NormalFont);
            wcf_left.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
            wcf_left.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
            wcf_left.setAlignment(Alignment.LEFT); // 文字水平对齐
            wcf_left.setWrap(false); // 文字是否换行
            //第一行
            for(int n = 0;n<homes.length;n++){
//                rowHome.createCell(n).setCellValue(homes[n]);
                sheet1.addCell(new Label(n, 0, homes[n], wcf_center));
            }
            //正文
            for(int i=0;i<resultList.size();i++)
            {
                Object [] os= resultList.get(i);
               // HSSFRow row = sheet.createRow(i+1);   //第9行...第n行

                for(int j = 0;j<os.length;j++){
                    if(os[j]!=null) {
                        if (j == 4 || j == 5) {
                            BigDecimal m = (BigDecimal) os[j];
                            sheet1.addCell(new Label(j,i+1,m.toString(), wcf_left));
//                            row.createCell(j).setCellValue(m.toString());
                        } else if(j == 7){
                            Integer status = Integer.parseInt(os[j].toString());
                            if(status ==  1){
                                sheet1.addCell(new Label(j,i+1,on, wcf_left));
//                                row.createCell(j).setCellValue(on);
                            }else {
                                sheet1.addCell(new Label(j,i+1,down, wcf_left));
//                                row.createCell(j).setCellValue(down);
                            }
                        }else if(j == 11){
                            Date date2 = (Date)os[j];
                            sheet1.addCell(new Label(j,i+1, sdf1.format(date2), wcf_left));
                        }else {
                            sheet1.addCell(new Label(j,i+1,os[j].toString(), wcf_left));
//                            row.createCell(j).setCellValue(os[j].toString());
                        }
                    }


                }
            }
            workbook.write();
//            wb.write(fileOut);
            //fileOut.close();
            workbook.close();
            System.out.print("OK");
        }catch(Exception e){
            e.printStackTrace();
        }
//        finally{
//            if(fileOut != null){
//                try {
//                    fileOut.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
        return fileName;

    }

    @Override
    public  Integer productPictureSort(String productId){
        return productDao.getProductPicture(productId);
    }

    @Override
    public void changePictureSort(String sourceId,String sourceSort,String targetId,String targetSort){
          ProductPicture source = (ProductPicture)xdoDao.getObject(ProductPicture.class.getName(),sourceId);
          ProductPicture target = (ProductPicture)xdoDao.getObject(ProductPicture.class.getName(),targetId);
          source.setSort(Integer.parseInt(targetSort));
          target.setSort(Integer.parseInt(sourceSort ));
        xdoDao.saveOrUpdateObject(source);
        xdoDao.saveOrUpdateObject(target);

    }
}
