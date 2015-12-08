package com.efeiyi.ec.website.order.controller;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.efeiyi.ec.organization.model.AddressCity;
import com.efeiyi.ec.organization.model.AddressProvince;
import com.efeiyi.ec.purchase.model.PurchaseOrder;
import com.efeiyi.ec.purchase.model.PurchaseOrderGift;
import com.efeiyi.ec.website.order.service.CartManager;
import com.efeiyi.ec.website.order.service.PaymentManager;
import com.efeiyi.ec.website.order.service.PurchaseOrderManager;
import com.efeiyi.ec.website.organization.util.AuthorizationUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ming800.core.base.service.BaseManager;
import com.ming800.core.p.service.AutoSerialManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/22 0022.
 */
@Controller
public class PurchaseOrderGiftController {

    @Autowired
    private BaseManager baseManager;

    @Autowired
    private PurchaseOrderManager purchaseOrderManager;

    @Autowired
    private AutoSerialManager autoSerialManager;

    @Autowired
    private CartManager cartManager;

    @Autowired
    private PaymentManager paymentManager;

    @RequestMapping("/giftReceive/{orderId}")
    public String receiveGift(HttpServletRequest request, @PathVariable String orderId, Model model) {
        PurchaseOrderGift purchaseOrderGift = (PurchaseOrderGift) baseManager.getObject(PurchaseOrderGift.class.getName(), orderId);
        PurchaseOrderDelivery purchaseOrderDelivery=purchaseOrderGift.getPurchaseOrderDeliveryList().get(0);
        if (purchaseOrderGift.getOrderType().equals("3") && purchaseOrderGift.getOrderStatus().equals(PurchaseOrder.ORDER_STATUS_WRGIFT)) {
            //判断是否是礼品订单 且可以被收礼
            model.addAttribute("purchaseOrder", purchaseOrderGift);
        }

        String lc = "";//物流公司
        String serial = "";//物流单号
        String content = "";//物流信息
        if(purchaseOrderDelivery!=null){
            serial = purchaseOrderDelivery.getSerial();
            lc = purchaseOrderDelivery.getLogisticsCompany();
            try {
                URL url = new URL("http://www.kuaidi100.com/applyurl?key=" + "f8e96a50d49ef863" + "&com=" + lc + "&nu=" + serial);
                URLConnection con = url.openConnection();
                con.setAllowUserInteraction(false);
                InputStream urlStream = url.openStream();
                byte b[] = new byte[10000];
                int numRead = urlStream.read(b);
                content = new String(b, 0, numRead);
                while (numRead != -1) {
                    numRead = urlStream.read(b);
                    if (numRead != -1) {
                        String newContent = new String(b, 0, numRead, "UTF-8");
                        content += newContent;
                    }
                }
                urlStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.addAttribute("content",content);
            model.addAttribute("serial",serial);
            model.addAttribute("lc",lc);
        }
        //优先判断是否是送礼人查看当前页面
        if (AuthorizationUtil.isAuthenticated() && AuthorizationUtil.getMyUser().getId().equals(purchaseOrderGift.getUser().getId())) {
            model.addAttribute("order", purchaseOrderGift);
            model.addAttribute("request","/purchaseOrder/giftView");
            return "/purchaseOrder/purchaseOrderGiftView";
        }
        if (!purchaseOrderGift.getOrderStatus().equals(PurchaseOrder.ORDER_STATUS_WPAY) && !purchaseOrderGift.getOrderStatus().equals(PurchaseOrder.ORDER_STATUS_WRGIFT)){
            model.addAttribute("purchaseOrder", purchaseOrderGift);
            model.addAttribute("request","/purchaseOrder/giftView");
            return "/purchaseOrder/giftView";
        }
        model.addAttribute("request","/purchaseOrder/receiveGift");
        return "/purchaseOrder/receiveGift";
    }


    private static String accessKeyId = "maTnALCpSvWjxyAy";
    private static String accessKeySecret = "0Ou6P67WhuSHESKrwJClFqCKo5BuBf";

    public String productPicture(PurchaseOrderGift purchaseOrderGift) throws Exception {
        String giftMessage = purchaseOrderGift.getGiftMessage();
        String productModelName = new String();
        BigDecimal productModelPrice = new BigDecimal("0");
        if ("1".equals(purchaseOrderGift.getShowGiftNameStatus())) {
            productModelName ="礼物清单："+ purchaseOrderGift.getPurchaseOrderProductList().get(0).getProductModel().getName();
        }
        if ("1".equals(purchaseOrderGift.getShowGiftPriceStatus())) {
            productModelPrice = purchaseOrderGift.getPurchaseOrderProductList().get(0).getProductModel().getPrice();
        }
        //背景图设置
        URL backgroundUrl = new URL("http://pro.efeiyi.com/gift/background.jpg");
        ImageIcon imgIcon = new ImageIcon(backgroundUrl);
        Image theImg = imgIcon.getImage();
        int width = 640;
        int height = 1136;
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimage.createGraphics();
        g.setColor(Color.black);
        g.drawImage(theImg, 0, 0, null);
        //设置字体、字型、字号
        g.setFont(new Font("宋体", Font.BOLD, 29));
        g.drawString(productName,420,270);
        g.drawString("【"+projectName+"】",400,340);
        g.setFont(new Font("宋体", Font.BOLD, 27));
        g.drawString(masterNamer,420,410);
        //背景图set文字显示
        g.setFont(new Font("宋体", Font.BOLD, 24));
        if (giftMessage != null) {
            if(giftMessage.length()<17){
                g.drawString(giftMessage, 240, 600);
            }
            if(giftMessage.length()<35){
                g.drawString(giftMessage.substring(0,17), 240, 600);
                g.drawString(giftMessage.substring(17,giftMessage.length()), 220, 630);
            }
            if(35<=giftMessage.length()&&giftMessage.length()<50){
                g.drawString(giftMessage.substring(0,17), 240, 600);
                g.drawString(giftMessage.substring(17,35), 220, 630);
                g.drawString(giftMessage.substring(35,giftMessage.length()), 220, 660);
            }
        }
        g.drawString("——"+sender,500,670);
        g.dispose();
        //二维码生成
        String content = "http://www2.efeiyi.com/giftReceive/" + purchaseOrderGift.getId();
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(content,
                    BarcodeFormat.QR_CODE, 154, 154, hints);//二维码像素
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int qRWidth = bitMatrix.getWidth();
        int qRHeight = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(qRWidth, qRHeight, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < qRWidth; x++) {
            for (int y = 0; y < qRHeight; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) == true ?
                        Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
//        String testurl = "http://pro.efeiyi.com/product/%E4%B8%BB%E5%9B%BE20151022173939.jpg@!product-hot";
        String imgName = urlString.substring(urlString.lastIndexOf("/")+1,urlString.length());
        String imgNameEncode = URLEncoder.encode(imgName,"UTF-8");
        urlString = urlString.substring(0,urlString.lastIndexOf("/")+1)+imgNameEncode;
        URL pictureUrl = new URL("http://pro.efeiyi.com/"+urlString+"@!gift-picture-sender");
//        URL pictureUrl = new URL(testurl);
        ImageIcon giftImgIcon = new ImageIcon(pictureUrl);
        BufferedImage combined = new BufferedImage(bimage.getWidth(), bimage.getHeight(), BufferedImage.TYPE_INT_RGB);
        //图像合并
        Graphics2D g1 = combined.createGraphics();
        g1.drawImage(bimage, 0, 0, null);
        g1.drawImage(image, 40, 580, null);
        g1.drawImage(giftImgIcon.getImage(), 40, 250, null);
        g1.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(combined, "jpg", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        os.close();
        ObjectMetadata meta = new ObjectMetadata();
        // 必须设置ContentLength
        meta.setContentLength(os.size());
        // 上传Object
        String url = "gift/" + purchaseOrderGift.getId() + ".jpg";
        OSSClient client = new OSSClient("http://oss-cn-beijing.aliyuncs.com", accessKeyId, accessKeySecret);
        PutObjectResult result = client.putObject("ec-efeiyi", url, is, meta);
        is.close();
        return url;
    }

    @RequestMapping({"/createGiftImage/{orderId}"})
    public String createGiftImage(@PathVariable String orderId, Model model) throws Exception {
        PurchaseOrderGift purchaseOrderGift = (PurchaseOrderGift) baseManager.getObject(PurchaseOrderGift.class.getName(), orderId);
        String url = productPicture(purchaseOrderGift);
        model.addAttribute("url", url);
        return "/purchaseOrder/giftImage";
    }


    @RequestMapping("/giftConfirm.do")
    public String confirmGift(HttpServletRequest request, Model model) {
        String purchaseOrderId = request.getParameter("purchaseOrderId");
        PurchaseOrderGift purchaseOrderGift = (PurchaseOrderGift) baseManager.getObject(PurchaseOrder.class.getName(), purchaseOrderId);
        AddressProvince addressProvince = (AddressProvince) baseManager.getObject(AddressProvince.class.getName(), request.getParameter("province.id"));
        AddressCity addressCity = (AddressCity) baseManager.getObject(AddressCity.class.getName(), request.getParameter("city.id"));
        String detail = request.getParameter("receiveDetail");
        String address = addressProvince.getName() + addressCity.getName() + detail;
        String receiveName = request.getParameter("receiveName");
        String receivePhone = request.getParameter("receivePhone");
        purchaseOrderGift.setReceiverName(receiveName);
        purchaseOrderGift.setReceiverPhone(receivePhone);
        purchaseOrderGift.setPurchaseOrderAddress(address);
        purchaseOrderGift.setOrderStatus(PurchaseOrder.ORDER_STATUS_WRECEIVE); //订单改为未发货状态
        baseManager.saveOrUpdate(PurchaseOrderGift.class.getName(), purchaseOrderGift);
        model.addAttribute("purchaseOrder", purchaseOrderGift);
        return "/purchaseOrder/giftView";
    }


}
