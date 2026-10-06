package br.senac.sp.tads.dsw.exemplo5.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.model.Funcionario;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import br.senac.sp.tads.dsw.exemplo5.repository.FuncionarioRepository;
import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;//Serve para usar o post no mockMvc.perform
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 
//Serve para usar o mockMvc.perform

import java.time.LocalDate;

@SpringBootTest  //Quando o teste for rodado, ele vai subir o servidor
@AutoConfigureMockMvc //Transforma parâmetros em uma requisição HTTP
@Transactional //Gerencia transações de banco de dados de forma declarativa
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

    @BeforeEach //Sempre executada antes dos outros métodos
    void setUp(){
         Departamento departamento = new Departamento();
         departamento.setNome("Departamento Padrão");
         departamento.setOrcamento(10000.00);
         departamentoPadrao = departamentoRepository.save(departamento);
        }

        @Test 
        void deveCriarFuncionarioComSucesso() throws Exception {
         //1 - Criar o objeto que queremos enviar
         Funcionario funcionario = new Funcionario();
         funcionario.setNome("João");
         funcionario.setDataContratacao(LocalDate.now());
         funcionario.setTrabalhoRemoto(true);
         funcionario.setDepartamento(departamentoPadrao);

         // 2 - Converter o objeto JAVA para JSON (String)
         String json = objectMapper.writeValueAsString(funcionario);

         //3 - Enviar o POST para a API
         mockMvc.perform(
            post("/api/funcionarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )

        // 4 Checar se a resposta é o que foi criado
        .andExpect(status().isCreated()) //201
        .andExpect(jsonPath("$.id").exists()) //Verifica se o ID existe
        .andExpect(jsonPath("$.nome").value("João")) // Verifica o nome
        .andExpect(jsonPath("$.dataContratacao").value(LocalDate.now()))
        .andExpect(jsonPath("$.trabalhoRemoto").value(true)) 
        .andExpect(jsonPath("$.departamento_id").value(departamentoPadrao.getId()));      
        } 
        

        @Test 
        void deveRetornarErro400AoCriarFuncionarioComDataFutura() throws Exception {

        }

        @Test 
        void deveListartodosOsFuncionarios() throws Exception{
         Funcionario funcionario = new Funcionario();
         funcionario.setNome("João");
         funcionario.setDataContratacao(LocalDate.now());
         funcionario.setTrabalhoRemoto(true);
         funcionario.setDepartamento(departamentoPadrao);

         mockMvc.perform(get("/api/funcionarios"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$[0]").exists());
        }

        @Test
        void deveBuscarFuncionarioPorId() throws Exception{
         Funcionario funcionario = new Funcionario();
         funcionario.setNome("João");
         funcionario.setDataContratacao(LocalDate.now());
         funcionario.setTrabalhoRemoto(true);
         funcionario.setDepartamento(departamentoPadrao);

         mockMvc.perform(get("/api/funcionarios/" + departamentoPadrao.getId()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").exists()) //Verifica se o ID existe
         .andExpect(jsonPath("$.nome").value("João")) // Verifica o nome
         .andExpect(jsonPath("$.dataContratacao").value(LocalDate.now()))
         .andExpect(jsonPath("$.trabalhoRemoto").value(true)) 
         .andExpect(jsonPath("$.departamento").value(departamentoPadrao));  
        }

        @Test 
        void deveAtualizarFuncionario() throws Exception{
         Funcionario funcionario = new Funcionario();
         funcionario.setNome("João");
         funcionario.setDataContratacao(LocalDate.now());
         funcionario.setTrabalhoRemoto(true);
         Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

         funcionarioSalvo.setNome("João Michael");

         String json = objectMapper.writeValueAsString(funcionarioSalvo);

         mockMvc.perform(
            put("/api/funcionarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )
         .andExpect(status().isOk()) //200
         .andExpect(jsonPath("$.nome").value("João")) // Verifica o nome
         .andExpect(jsonPath("$.dataContratacao").value(LocalDate.now()))
         .andExpect(jsonPath("$.trabalhoRemoto").value(true)) 
         .andExpect(jsonPath("$.departamento").value(departamentoPadrao));  
        }

        @Test 
        void deveApagarFuncionario() throws Exception{
         Funcionario funcionario = new Funcionario();
         funcionario.setNome("João");
         funcionario.setDataContratacao(LocalDate.now());
         funcionario.setTrabalhoRemoto(true);
         Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

         mockMvc.perform(
            delete("/api/funcionarios/" + funcionarioSalvo.getId()))
            .andExpect(status().isNoContent());

        mockMvc.perform(
            get("/api/funcionarios/" + funcionarioSalvo.getId()))
            .andExpect(status().isNotFound());
        }

}
