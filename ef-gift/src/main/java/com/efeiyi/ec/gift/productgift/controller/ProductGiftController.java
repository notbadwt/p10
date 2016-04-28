package com.efeiyi.ec.gift.productgift.controller;

import com.efeiyi.ec.product.model.Subject;
import com.ming800.core.base.service.BaseManager;
import com.ming800.core.p.model.Banner;
import com.ming800.core.p.service.BannerManager;
import com.ming800.core.p.service.ObjectRecommendedManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2016/4/19.
 */
@Controller
public class ProductGiftController {
    @Autowired
    private BannerManager bannerManager;
    @Autowired
    private ObjectRecommendedManager objectRecommendedManager;
    @Autowired
    private BaseManager baseManager;

    @RequestMapping({"/getProductGiftList"})
    public String testAspect(HttpServletRequest request, Model model) throws Exception{
        //获取页头轮播图
        List<Banner> bannerList = bannerManager.getBannerList("productGiftList");
        //获取礼品频道推荐专题
        List<Subject> subjectList = objectRecommendedManager.getRecommendedList("productGiftRecommendedUp");
        List<Subject> subjectList1 = objectRecommendedManager.getRecommendedList("productGiftRecommendedDown");
        model.addAttribute("bannerList", bannerList);
        model.addAttribute("subjectList", subjectList);
        model.addAttribute("subjectList1", subjectList1);
        return "/gift/productGiftList";
    }

    @RequestMapping({"/viewSubject/{subjectId}"})
         public String viewSubject(@PathVariable String subjectId, Model model) throws Exception{
        Subject subject = (Subject) baseManager.getObject(Subject.class.getName(),subjectId);
        model.addAttribute("subject", subject);
        if (subjectId.equals("")){
            return "/guoliyishiView";
        }else if (subjectId.equals("1")){
            return "/lishangwanglaiView";
        }else {
            return "";
        }
    }

}