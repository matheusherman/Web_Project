package com.puc.project.rentify.controller;

import com.puc.project.rentify.model.ApiError;
import com.puc.project.rentify.model.Imovel;
import com.puc.project.rentify.model.ImovelRegistro;
import com.puc.project.rentify.model.Locadora;
import com.puc.project.rentify.repository.ImovelRepository;
import com.puc.project.rentify.repository.LocadoraRepository;
import com.puc.project.rentify.repository.ReservaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/imoveis")
@Tag(name = "Imóvel", description = "API Imóvel contendo todos os processos relacionado.")
public class ImovelController {
    @Autowired
    ImovelRepository repository;
    @Autowired
    LocadoraRepository locadoraRepository;
    @Autowired
    ReservaRepository reservaRepository;
    @PersistenceContext
    EntityManager entity;

    @GetMapping("/")
    @Operation(summary = "Listar Imóveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de Imóveis"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Object> findAll() {
        try {
            List<Imovel> data = new ArrayList<Imovel>();
            repository.findAll().forEach(data::add);
            if (data.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Pesquisar Imóvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesquisa por um Imóvel usando qualquer parâmetro"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/search")
    public ResponseEntity<Object> findBy(@RequestParam Map<String, String> allParams) {
        try {
            if (allParams.containsKey("id")) {
                try {
                    Optional<Imovel> single_data = repository.findById(Long.valueOf(allParams.get("id")));
                    if (single_data.isPresent()){
                        List<Imovel> data = new ArrayList<>();
                        data.add(single_data.get());
                        return new ResponseEntity<>(data, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                }
            } else if (!allParams.isEmpty()) {
                CriteriaBuilder criteria = entity.getCriteriaBuilder();
                CriteriaQuery<Imovel> query = criteria.createQuery(Imovel.class);
                Root<Imovel> root = query.from(Imovel.class);
                List<Predicate> predicates = new ArrayList<>();
                if (allParams.containsKey("preco_dia")) {
                    predicates.add(criteria.equal(root.get("preco_dia"), Double.valueOf(allParams.get("preco_dia"))));
                }
                if (allParams.containsKey("tipo")) {
                    predicates.add(criteria.equal(root.get("tipo"), allParams.get("tipo")));
                }
                if (allParams.containsKey("num_quartos")) {
                    predicates.add(criteria.equal(root.get("num_quartos"), Integer.valueOf(allParams.get("num_quartos"))));
                }
                if (allParams.containsKey("num_banheiros")) {
                    predicates.add(criteria.equal(root.get("num_banheiros"), Integer.valueOf(allParams.get("num_banheiros"))));
                }
                if (allParams.containsKey("varanda")) {
                    predicates.add(criteria.equal(root.get("varanda"), allParams.get("varanda")));
                }
                if (allParams.containsKey("suites")) {
                    predicates.add(criteria.equal(root.get("suites"), Integer.valueOf(allParams.get("suites"))));
                }
                if (allParams.containsKey("garagem")) {
                    predicates.add(criteria.equal(root.get("garagem"), Integer.valueOf(allParams.get("garagem"))));
                }
                if (allParams.containsKey("tamanho")) {
                    predicates.add(criteria.equal(root.get("tamanho"), allParams.get("tamanho")));
                }
                if (allParams.containsKey("imobiliado")) {
                    predicates.add(criteria.equal(root.get("imobiliado"), allParams.get("imobiliado")));
                }
                if (allParams.containsKey("descricao")) {
                    predicates.add(criteria.equal(root.get("descricao"), allParams.get("descricao")));
                }
                if (allParams.containsKey("cep")) {
                    predicates.add(criteria.equal(root.get("cep"), allParams.get("cep")));
                }
                if (allParams.containsKey("numero")) {
                    predicates.add(criteria.equal(root.get("numero"), allParams.get("numero")));
                }
                if (allParams.containsKey("complemento")) {
                    predicates.add(criteria.equal(root.get("complemento"), allParams.get("complemento")));
                }
                if (allParams.containsKey("locadora")) {
                    predicates.add(criteria.equal(root.get("locadora").get("id"), Long.valueOf(allParams.get("locadora"))));
                }
                query.where(predicates.toArray(new Predicate[0]));
                List<Imovel> result = entity.createQuery(query).getResultList();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar Imóvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imóvel criada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ImovelRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "406", description = "Locadora não existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody ImovelRegistro registro) {
        try {
            if (!locadoraRepository.existsById(registro.getLocadora_id())) {
                return new ResponseEntity<>(new ApiError("406", "Locadora não existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Imovel imovel = new Imovel();
                imovel.setPreco_dia(registro.getPreco_dia());
                imovel.setTipo(registro.getTipo());
                imovel.setNum_quartos(registro.getNum_quartos());
                imovel.setNum_banheiros(registro.getNum_banheiros());
                imovel.setVaranda(registro.getVaranda());
                imovel.setGaragem(registro.getGaragem());
                imovel.setImobiliado(registro.getImobiliado());
                imovel.setDescricao(registro.getDescricao());
                imovel.setCep(registro.getCep());
                imovel.setNumero(registro.getNumero());
                imovel.setComplemento(registro.getComplemento());
                imovel.setLocadora(locadoraRepository.findById(registro.getLocadora_id()).get());
                return new ResponseEntity<>(repository.save(imovel), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Alterar Imóvel pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imóvel alterado", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ImovelRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "Imóvel id não encontrado"),
            @ApiResponse(responseCode = "406", description = "Locadora não existe")
    })
    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestParam long id, @RequestBody ImovelRegistro registro) {
        Optional<Imovel> data = repository.findById(id);
        if (data.isPresent()) {
            if (!locadoraRepository.existsById(registro.getLocadora_id())) {
                return new ResponseEntity<>(new ApiError("406", "Locadora não existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Imovel imovel = data.get();
                Locadora locadora = imovel.getLocadora();
                locadoraRepository.findById(locadora.getId());
                imovel.setPreco_dia(registro.getPreco_dia());
                imovel.setTipo(registro.getTipo());
                imovel.setNum_quartos(registro.getNum_quartos());
                imovel.setNum_banheiros(registro.getNum_banheiros());
                imovel.setVaranda(registro.getVaranda());
                imovel.setGaragem(registro.getGaragem());
                imovel.setImobiliado(registro.getImobiliado());
                imovel.setDescricao(registro.getDescricao());
                imovel.setCep(registro.getCep());
                imovel.setNumero(registro.getNumero());
                imovel.setComplemento(registro.getComplemento());
                imovel.setLocadora(locadoraRepository.findById(registro.getLocadora_id()).get());
                return new ResponseEntity<>(repository.save(imovel), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletar Imóvel por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imóvel deletada"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "ID não existente"),
            @ApiResponse(responseCode = "406", description = "ID sendo utilizado na tabela Reserva"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/")
    public ResponseEntity<Object> delete(@RequestParam long id) {
        try {
            Optional<Imovel> imovel = repository.findById(id);
            if (imovel.isPresent()) {
                if (reservaRepository.existsReservaByImovel(imovel.get())) {
                    return new ResponseEntity<>(new ApiError("406", "ID sendo utilizado na tabela 'reserva'"), HttpStatus.NOT_ACCEPTABLE);
                } else {
                    repository.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
