package com.br.itsingular.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.br.itsingular.entity.Empresa;

/**
 * @author guilherme
 *
 */
@Repository
public interface EmpresaRepository extends MongoRepository<Empresa, String> {

	List<Empresa> findByCnpj(String filtro);
	
}
