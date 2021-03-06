package com.br.itsingular.controller;

import java.time.LocalDate;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.br.itsingular.entity.Login;
import com.br.itsingular.entity.Requisicao;
import com.br.itsingular.entity.Tecnologias;
import com.br.itsingular.services.CadastrarTecnologiasServices;
import com.br.itsingular.services.EmailServices;
import com.br.itsingular.services.RequisicaoServices;
import com.br.itsingular.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/requisicao")
public class RequisicaoController {
	
	@Autowired
	private RequisicaoServices requisicaoServices;
	
	@Autowired
	private HttpSession session;

	@Autowired
	private CadastrarTecnologiasServices cadastrarTecnologiasServices;
	
	@Autowired
	private EmailServices emailServices;

	@RequestMapping(value = "/abrir", method = RequestMethod.GET)
	public ModelAndView init( Requisicao requisicao) {
		ModelAndView modelAndView = new ModelAndView("RequisicaoVagas");
		Login login = (Login) session.getAttribute("login");
		modelAndView.addObject("login", login);
		
		if(!Utils.isEmptyOrNull(requisicao)) { 
			requisicao = new Requisicao();
			
			requisicao.setNomeSolicitante(login.getName());
			requisicao.setLoginSolicitante(login.getUsername());
			requisicao.setDataSolicitacao(LocalDate.now());
			modelAndView.addObject("requisicao", requisicao);
			
		}
		modelAndView.addObject("listTecnologias",  listTecnologias());
		return modelAndView;
	}
	
	@RequestMapping(path = "/addRequisicao", method = RequestMethod.POST)
	public ModelAndView addRequisicao(@Valid Requisicao requisicao, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("RequisicaoVagas");
		modelAndView.addObject("login", session.getAttribute("login"));
		String mensagem = null;
		if (result.hasErrors()) {
			return init(null);
		}
		modelAndView.addObject("listTecnologias",  listTecnologias());
		if (!Utils.isEmptyOrNull(requisicaoServices.salvarRequisicao(requisicao))) {
			mensagem = "Success";
			try {
				enviarEmailPosRequisicao(Integer.valueOf(requisicao.getVagas()),requisicao.getNomeSolicitante(), 
						requisicao.getCliente(),requisicao.getDataSolicitacao());
				modelAndView.addObject("requisicao", new Requisicao());
				mensagem = "emailSuccess";
			} catch (Exception e) {
				log.error("###### -- FALHA! Cause:" + e.getCause() + "--> Mensagem" + e.getMessage());
				mensagem = "emailErro";
			}
		}
		
		modelAndView.addObject("message",mensagem);
		return modelAndView;
	}
	public List<Tecnologias> listTecnologias(){
		return cadastrarTecnologiasServices.findTecnologias();
	}
	public void enviarEmailPosRequisicao(int quantidadeVagas, String requisitante, String cliente, LocalDate dataAbertura) throws MessagingException {
		emailServices.enviarEmailRequisicao(quantidadeVagas, dataAbertura, requisitante, cliente);
	}
}
