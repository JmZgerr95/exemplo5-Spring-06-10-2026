package br.senac.sp.tads.dsw.exemplo5.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;//Serve para usar o post no mockMvc.perform
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 
//Serve para usar o mockMvc.perform

@SpringBootTest  //Quando o teste for rodado, ele vai subir o servidor
@AutoConfigureMockMvc //Transforma parâmetros em uma requisição HTTP
@Transactional //Gerencia transações de banco de dados de forma declarativa
public class DepartamentoControllerTest {
 
    @Autowired 
    private MockMvc mockMvc;

    @Autowired 
    private DepartamentoRepository repository;

    @Autowired 
    private ObjectMapper objectMapper;

    @Test 
    void deveCriarDepartamentoComSucesso() throws Exception{
         //O teste precisa de um método com nome BEM claro.

         //1 - Criar o objeto que queremos enviar
         Departamento departamento = new Departamento();
         departamento.setNome("Turma B - TADS");
         departamento.setOrcamento(3600.00);

         // 2 - Converter o objeto JAVA para JSON (String)
         String json = objectMapper.writeValueAsString(departamento);

         //3 - Enviar o POST para a API
         mockMvc.perform(
            post("/api/departamentos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )

        // 4 Checar se a resposta é o que foi criado
        .andExpect(status().isCreated()) //201
        .andExpect(jsonPath("$.id").exists()) //Verifica se o ID existe
        .andExpect(jsonPath("$.nome").value("Turma B - TADS")) // Verifica o nome
        .andExpect(jsonPath("$.orcamento").value(3600.00)); // Verifica o orçamento    
        } 

        @Test 
        void deveListarTodosOsDepartamentos() throws Exception{
         Departamento departamento = new Departamento();
         departamento.setNome("Turma B - TADS");
         departamento.setOrcamento(3600.00);
         repository.save(departamento); 

         mockMvc.perform(get("/api/departamentos"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$[0]").exists());
        }

        @Test 
        void deveBuscarDepartamentoPorId() throws Exception{
         Departamento departamento = new Departamento();
         departamento.setNome("Turma B - TADS");
         departamento.setOrcamento(3600.00);
         Departamento departamentoSalvo = repository.save(departamento); 
         
         mockMvc.perform(get("/api/departamentos/" + departamento.getId()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(departamento.getId()))
         .andExpect(jsonPath("$.nome").value("Turma B - TADS")) // Verifica o nome
         .andExpect(jsonPath("$.orcamento").value(3600.00)); // Verifica o orçamento  
        }

        @Test 
        void deveAtualizarDepartamento() throws Exception{
         Departamento departamento = new Departamento();
         departamento.setNome("Turma B - TADS");
         departamento.setOrcamento(3600.00);
         Departamento departamentoSalvo = repository.save(departamento);

         departamentoSalvo.setNome("Turma do Luis");

        String json = objectMapper.writeValueAsString(departamentoSalvo);

         mockMvc.perform(
            put("/api/departamentos/" + departamentoSalvo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )
        .andExpect(status().isOk()) //200
        .andExpect(jsonPath("$.nome").value("Turma B - TADS")) // Verifica o nome
        .andExpect(jsonPath("$.orcamento").value(3600.00)); // Verifica o orçamento    
        } 

        @Test 
        void deveApagarDepartamento() throws Exception{
        Departamento departamento = new Departamento();
         departamento.setNome("Turma B - TADS");
         departamento.setOrcamento(3600.00);
         Departamento departamentoSalvo = repository.save(departamento);

         mockMvc.perform(
            delete("/api/departamentos/" + departamentoSalvo.getId()))
            .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/departamentos/" + departamentoSalvo.getId()))
            .andExpect(status().isNotFound());
        }
}
