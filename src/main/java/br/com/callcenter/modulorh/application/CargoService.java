package br.com.callcenter.modulorh.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulorh.api.dto.CargoDto;
import br.com.callcenter.modulorh.domain.entity.Cargo;
import br.com.callcenter.modulorh.infrastructure.repository.CargoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CargoService {

    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public List<Cargo> listarTodos() {
        return cargoRepository.findAll();
    }

    public Cargo buscarPorId(UUID id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));
    }

    public Cargo criar(CargoDto dto) {
        Cargo cargo = Cargo.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .salarioBase(dto.getSalarioBase())
                .build();
        return cargoRepository.save(cargo);
    }

    public Cargo atualizar(UUID id, CargoDto dto) {
        Cargo existente = buscarPorId(id);
        existente.setNome(dto.getNome());
        existente.setDescricao(dto.getDescricao());
        existente.setSalarioBase(dto.getSalarioBase());
        return cargoRepository.save(existente);
    }

    public void deletar(UUID id) {
        Cargo existente = buscarPorId(id);
        cargoRepository.delete(existente);
    }
}

