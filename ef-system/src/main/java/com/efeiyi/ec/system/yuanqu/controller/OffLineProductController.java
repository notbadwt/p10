package com.efeiyi.ec.system.yuanqu.controller;

import com.efeiyi.ec.master.model.Master;
import com.efeiyi.ec.organization.model.Image;
import com.efeiyi.ec.organization.model.ImagePanel;
import com.efeiyi.ec.organization.model.Panel;
import com.efeiyi.ec.product.model.Product;
import com.efeiyi.ec.product.model.ProductModel;
import com.efeiyi.ec.tenant.model.BigTenant;
import com.efeiyi.ec.tenant.model.Tenant;
import com.ming800.core.base.service.BaseManager;
import com.ming800.core.does.model.PageInfo;
import com.ming800.core.p.PConst;
import com.ming800.core.p.service.AliOssUploadManager;
import com.ming800.core.p.service.AutoSerialManager;
import com.ming800.core.taglib.PageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

@Controller
@RequestMapping({"/yuanqu/product"})
public class OffLineProductController {

    @Autowired
    private BaseManager baseManager;

    @Autowired
    private AliOssUploadManager aliOssUploadManager;

    @Autowired
    private AutoSerialManager autoSerialManager;

    @RequestMapping({"/getProductById"})
    @ResponseBody
    public Object getProductById(HttpServletRequest request) {
        String id = request.getParameter("id");
        return baseManager.getObject(Product.class.getName(), id);
    }

    @RequestMapping({"/baseSubmit"})
    @ResponseBody
    public Object baseSubmit(HttpServletRequest request, MultipartRequest multipartRequest) {
        Product product;
        if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
            product = (Product) baseManager.getObject(Product.class.getName(), request.getParameter("id"));
        } else {
            product = new Product();
        }
        product.setName(request.getParameter("name"));
        product.setSubName(request.getParameter("subName"));
        product.setCreateDateTime(new Date());
        product.setStatus(Product.PRODUCT_STATUS_DOWN);
        product.setType(Product.PRODUCT_TYPE_OFFLINE);
        if (request.getParameter("tenantId") != null) {
            BigTenant tenant = (BigTenant) baseManager.getObject(BigTenant.class.getName(), request.getParameter("tenantId"));
            product.setBigTenant(tenant);
        }
        baseManager.saveOrUpdate(Product.class.getName(), product);
        String pictureUrl = uploadImage(multipartRequest.getFile("picture_url"));//主图
        if (!"".equals(pictureUrl)) {
            Image image = new Image(product.getName(), pictureUrl, product.getId(), "1", "1");
            baseManager.saveOrUpdate(Image.class.getName(), image);
        }
        String audioUrl = uploadImage(multipartRequest.getFile("audio"));
        if (!"".equals(audioUrl)) {
            Image audio = new Image(product.getName() + "_audio", audioUrl, product.getId(), "1", "2");
            baseManager.saveOrUpdate(Image.class.getName(), audio);
        }
        return product;
    }


    @RequestMapping({"/getProductList"})
    @ResponseBody
    public Object getProductList(HttpServletRequest request) {
        int limit = Integer.parseInt(request.getParameter("limit"));
        int offset = Integer.parseInt(request.getParameter("offset"));
        String name = request.getParameter("name");
        String tenantId = request.getParameter("tenantId");
        String hql = "select obj from Product obj where obj.type='" + Product.PRODUCT_TYPE_OFFLINE + "' and obj.status!='0'";
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        if (name != null) {
            hql += " and obj.name=:name";
            param.put("name", name);
        }
        if (tenantId != null) {
            hql += " and obj.tenant.id=:tenantId";
            param.put("tenantId", tenantId);
        }
        hql += " order by obj.createDateTime desc";
        PageEntity pageEntity = new PageEntity();
        pageEntity.setSize(limit);
        pageEntity.setrIndex(offset);
        PageInfo pageInfo = baseManager.listPageInfo(hql, pageEntity, param);
        return pageInfo.getList();
    }

    @RequestMapping({"/getProductModelList"})
    @ResponseBody
    public Object getProductModelList(HttpServletRequest request) {
        String productId = request.getParameter("productId");
        Product product = (Product) baseManager.getObject(Product.class.getName(), productId);
        return product.getProductModelList();
    }


    @RequestMapping({"/modelSubmit"})
    @ResponseBody
    public Object modelSubmit(HttpServletRequest request, MultipartRequest multipartRequest) throws Exception {
        String id = request.getParameter("id");
        String productId = request.getParameter("productId");
        Product product = (Product) baseManager.getObject(Product.class.getName(), productId);
        ProductModel productModel;
        if (id == null) {
            productModel = (ProductModel) baseManager.getObject(ProductModel.class.getName(), id);
        } else {
            productModel = new ProductModel();
        }
        productModel.setSerial(autoSerialManager.nextSerial("product"));
        productModel.setName(request.getParameter("name"));
        String amountStr = request.getParameter("amount");
        productModel.setAmount(amountStr != null ? Integer.parseInt(amountStr) : 1);
        productModel.setMarketPrice(new BigDecimal(request.getParameter("marketPrice")));
        productModel.setPrice(new BigDecimal(request.getParameter("price")));
        productModel.setProduct(product);
        productModel.setStatus("1");
        productModel.setProductModel_url(uploadImage(multipartRequest.getFile("productModel_url")));
        baseManager.saveOrUpdate(ProductModel.class.getName(), productModel);
        return productModel;
    }


    @RequestMapping({"/masterSubmit"})
    @ResponseBody
    public Object masterSubmit(HttpServletRequest request) {
        String id = request.getParameter("id");
        String masterId = request.getParameter("masterId");
        if (id == null || masterId == null) {
            return null;
        }
        Product product = (Product) baseManager.getObject(Product.class.getName(), id);
        Master master = (Master) baseManager.getObject(Master.class.getName(), masterId);
        product.setMaster(master);
        baseManager.saveOrUpdate(Product.class.getName(), product);
        return product;
    }

    @RequestMapping({"/getProductModelById"})
    @ResponseBody
    public Object getProductModelById(HttpServletRequest request) {
        String id = request.getParameter("id");
        ProductModel productModel = (ProductModel) baseManager.getObject(ProductModel.class.getName(), id);
        productModel.getProduct();
        productModel.getProduct().getTenant();
        return productModel;
    }

    @RequestMapping({"/getPanelListByProductModel"})
    @ResponseBody
    public Object getPanelListByProductModel(HttpServletRequest request) {
        String productModelId = request.getParameter("id");
        String hql = "select obj from Panel obj where obj.owner=:productModelId and obj.status='1'";
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        param.put("productModelId", productModelId);
        return baseManager.listObject(hql, param);
    }

    @RequestMapping({"/panelSubmit"})
    @ResponseBody
    public Object panelSubmit(HttpServletRequest request, MultipartRequest multipartRequest) throws Exception {
        Panel panel = new Panel();
        panel.setStatus("1");
        panel.setType("1");
        panel.setName(request.getParameter("name"));
        panel.setOwner(request.getParameter("id"));
        panel.setContent(request.getParameter("content"));
        baseManager.saveOrUpdate(Panel.class.getName(), panel);
        for (MultipartFile multipartFile : multipartRequest.getFiles("imageList")) {
            String oName = multipartFile.getOriginalFilename();
            String nName;
            try {
                nName = System.currentTimeMillis() + "" + (int) (Math.random() * 1000000) + "." + oName.split("\\.")[1];
            } catch (Exception e) {
                continue;
            }
            String url = "image/" + nName;
            aliOssUploadManager.uploadFile(multipartFile, "ef-wiki", url);
            String fullUrl = PConst.OSS_EF_WIKI_HOST + url;
            Image image = new Image();
            image.setStatus("1");
            image.setType("1");
            image.setOwner(panel.getId());
            image.setCreateTime(new Date());
            image.setSrc(fullUrl);
            ImagePanel imagePanel = new ImagePanel();
            imagePanel.setImage(image);
            imagePanel.setPanel(panel);
            baseManager.saveOrUpdate(Image.class.getName(), image);
            baseManager.saveOrUpdate(ImagePanel.class.getName(), imagePanel);
        }

        MultipartFile multipartFile = multipartRequest.getFile("media");
        if (multipartFile != null) {
            String oName = multipartFile.getOriginalFilename();
            String nName;
            String fullUrl = null;
            try {
                nName = System.currentTimeMillis() + "" + (int) (Math.random() * 1000000) + "." + oName.split("\\.")[1];
                String url = "image/" + nName;
                aliOssUploadManager.uploadFile(multipartFile, "ef-wiki", url);
                fullUrl = PConst.OSS_EF_WIKI_HOST + url;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Image image = new Image();
            image.setStatus("1");
            image.setType("2");
            image.setOwner(panel.getId());
            image.setCreateTime(new Date());
            image.setSrc(fullUrl);
            baseManager.saveOrUpdate(Image.class.getName(), image);
            panel.setMedia(image);
            baseManager.saveOrUpdate(Panel.class.getName(), panel);
        }
        return panel;
    }


    private String uploadImage(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return "";
        }
        String oName = multipartFile.getOriginalFilename();
        String nName;
        try {
            nName = System.currentTimeMillis() + "" + (int) (Math.random() * 1000000) + "." + oName.split("\\.")[1];
        } catch (Exception e) {
            return "";
        }
        String url = "image/" + nName;
        try {
            boolean uploadSuccess = aliOssUploadManager.uploadFile(multipartFile, "ef-wiki", url);
            if (!uploadSuccess) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        return PConst.OSS_EF_WIKI_HOST + url;
    }

}
