package com.puc.project.rentify.repository;

import com.puc.project.rentify.model.Imovel;
import com.puc.project.rentify.model.Locatario;
import com.puc.project.rentify.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Boolean existsReservaByLocatario(Locatario locatario);
    Boolean existsReservaByImovel(Imovel imovel);
    @Query("SELECT r FROM Reserva AS r WHERE r.imovel = :imovel AND ((data_inicio between :data_inicio and :data_fim) or (data_fim between :data_inicio and :data_fim))")
    List<Reserva> existsReservaByImovelInRangeDataInicio(Imovel imovel, LocalDate data_inicio, LocalDate data_fim);
}
