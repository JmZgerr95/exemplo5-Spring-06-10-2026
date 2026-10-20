package br.senac.sp.tads.dsw.exemplo5.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.model.Funcionario;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import br.senac.sp.tads.dsw.exemplo5.repository.FuncionarioRepository;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
public class FuncionarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired 
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Departamento departamentoPadrao;

    @BeforeEach 
    void setUp(){
        Departamento departamento = new Departamento();
        departamento.setNome("Departamento Padrão");
        departamento.setOrcamento(10000.00);
        departamentoPadrao = departamentoRepository.save(departamento);
    }

    @Test 
    void deveCriarFuncionarioComSucesso() throws Exception{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("João Michael");
        funcionario.setDataContratacao(LocalDate.now());
        funcionario.setTrabalhoRemoto(true);
        funcionario.setDepartamento(departamentoPadrao);
        
        String json = objectMapper.writeValueAsString(funcionario);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value(funcionario.getNome()))
                .andExpect(jsonPath("$.dataContratacao").value(funcionario.getDataContratacao().toString()))
                .andExpect(jsonPath("$.trabalhoRemoto").value(funcionario.getTrabalhoRemoto()))
                .andExpect(jsonPath("$.departamento.id").value(funcionario.getDepartamento().getId()));
    }

    @Test 
    void deveRetornarErro400AoCriarFuncionarioComDataFutura() throws Exception{
        LocalDate data = LocalDate.parse("2100-10-20");
        
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("João Michael");
        funcionario.setDataContratacao(data);
        funcionario.setTrabalhoRemoto(true);
        funcionario.setDepartamento(departamentoPadrao);
        
        String json = objectMapper.writeValueAsString(funcionario);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test 
    void deveListarTodosOsFuncionarios() throws Exception{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("João Michael");
        funcionario.setDataContratacao(LocalDate.now());
        funcionario.setTrabalhoRemoto(true);
        funcionario.setDepartamento(departamentoPadrao);
        funcionarioRepository.save(funcionario);

        mockMvc.perform(get("/api/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test 
    void deveBuscarFuncionarioPorId() throws Exception{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("João Michael");
        funcionario.setDataContratacao(LocalDate.now());
        funcionario.setTrabalhoRemoto(true);
        funcionario.setDepartamento(departamentoPadrao);
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        mockMvc.perform(get("/api/funcionarios/" + funcionarioSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(funcionarioSalvo.getId()))
                .andExpect(jsonPath("$.nome").value(funcionarioSalvo.getNome()))
                .andExpect(jsonPath("$.trabalhoRemoto").value(funcionarioSalvo.getTrabalhoRemoto()))
                .andExpect(jsonPath("$.departamento.id").value(funcionarioSalvo.getDepartamento().getId()));
    }

    @Test
    void deveAtualizarFuncionario() throws Exception{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("João Michael");
        funcionario.setDataContratacao(LocalDate.now());
        funcionario.setTrabalhoRemoto(true);
        funcionario.setDepartamento(departamentoPadrao);
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        funcionarioSalvo.setNome("Anderson Gabriel");

        String json = objectMapper.writeValueAsString(funcionarioSalvo);

        mockMvc.perform(put("/api/funcionarios/" + funcionarioSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Anderson Gabriel"));
    }

    @Test 
    void deveApagarFuncionario() throws Exception{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("João Michael");
        funcionario.setDataContratacao(LocalDate.now());
        funcionario.setTrabalhoRemoto(true);
        funcionario.setDepartamento(departamentoPadrao);
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        mockMvc.perform(delete("/api/funcionarios/" + funcionarioSalvo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/funcionarios/" + funcionarioSalvo.getId()))
                .andExpect(status().isNotFound());
    }
}
