package com.dmt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dmt.bean.KhachHangBean;
import com.dmt.bo.DangKyBo;
//import com.dmt.bo.KhachHangBo;
import com.dmt.bo.KhachHangBo;
import com.dmt.bo.ListGmailBo;
import com.dmt.utills.MD5;
import com.dmt.utills.VerifyUtils;

@Controller
public class KhachHangController {

	@RequestMapping("/Login")
	public String voidProduct() {
		return "Login";
	}

	@RequestMapping("/DangXuat")
	public String LogoutPage(HttpServletRequest request, HttpSession session) {
		if (session.getAttribute("khachhang") != null) {
			session.removeAttribute("khachhang");
		}
		System.out.println("123");
		return "About";
	}

	@RequestMapping(value = "/KiemTra", method = RequestMethod.POST)
	public String voidKiemtra(HttpServletRequest request, HttpSession session) {
		try {
			Boolean valid = true;
			MD5 md5 = new MD5();
			String username = request.getParameter("Email");
			String password = request.getParameter("password");

			if (username != null && password != null) {
				String passwordDaBam = md5.getHashPass(password);
				KhachHangBo bo = new KhachHangBo();
				KhachHangBean kh = bo.ktdm(username, passwordDaBam);
				if (kh != null) {
					String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
					System.out.println("gRecaptchaResponse" + gRecaptchaResponse);
					// Verify CAPTCHA.
					valid = VerifyUtils.verify(gRecaptchaResponse);
					if (valid == true) {
						session.setAttribute("khachhang", kh);
						if (session.getAttribute("giohang") != null) {
							session.removeAttribute("giohang");
						}
					} else {
						request.setAttribute("SaiCapcha", "1");
					}
				} else {
					request.setAttribute("saidangnhap", "1");
				}
			}

			return "About";
		} catch (Exception e) {
			return null;
		}

	}

	@RequestMapping(value = "/DangKy", method = RequestMethod.POST)
	public String DangKy(HttpServletRequest request, HttpSession session) throws Exception {
		ListGmailBo gmailbo = new ListGmailBo();
		List<String> ds = gmailbo.getGmailDao();
		DangKyBo bo = new DangKyBo();
		int x = 0;
		int y = 0;
		try {
			request.setCharacterEncoding("UTF-8");
			MD5 md5 = new MD5();
			String HoTen = request.getParameter("HoTen");
			String SoDienThoai = request.getParameter("SoDienThoai");
			String DiaChi = request.getParameter("DiaChi");
			String TenDangNhap = request.getParameter("TenDangNhap");
			String MatKhau = request.getParameter("MatKhau");
			String MatKhauDaBam = md5.getHashPass(MatKhau);
			for (int i = 0; i < ds.size(); i++) {
				System.out.println(ds.get(i));
				if (TenDangNhap.equals(ds.get(i))) {
					y = 1;
				}
			}
			if (y == 0) {
				x = bo.DangKyDao(HoTen, SoDienThoai, DiaChi, TenDangNhap, MatKhauDaBam);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (y == 1) {
			request.setAttribute("dangkythatbai", "Trùng tên đăng ký");
		}
		request.setAttribute("dangkythanhcong", "123");
		return "About";

	}

}
