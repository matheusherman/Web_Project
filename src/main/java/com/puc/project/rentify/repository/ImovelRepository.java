package com.puc.project.rentify.repository;

import com.puc.project.rentify.model.Imovel;
import com.puc.project.rentify.model.Locadora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelRepository extends JpaRepository<Imovel, Long> {
    Boolean existsImovelByLocadora(Locadora locadora);
}