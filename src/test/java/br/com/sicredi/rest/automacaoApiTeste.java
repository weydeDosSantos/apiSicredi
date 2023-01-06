package br.com.sicredi.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.javafaker.Faker;

import br.com.sicredi.core.BaseTestes;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class automacaoApiTeste extends BaseTestes{
	static Faker faker = new Faker();

	private static String CONTA_NAME = "conta" + System.nanoTime();
	private static Integer CONTA_ID;
	private static String CONTA_CPF =  faker.number().digits(11);
	
	
	
	@Test
	public void consultarRestricaoCpf() {
		given()
		
		.when()
			.get("restricoes/97093236014")
		.then()
			.statusCode(200)
			.body("mensagem",is("O CPF 97093236014 tem problema"));	
	}
	
	@Test
	public void consultarCpfSemRestricao() {
		given()
		.when()
			.get("restricoes/"+CONTA_CPF+"")
		.then()
			.statusCode(204);
	}

	@Test
	public void deveCriarSimulacao() {
		CONTA_ID =	given()
			.body("{ \"nome\": \""+CONTA_NAME+"\", \"cpf\": "+CONTA_CPF+",\"email\": \"email@email.com\", \"valor\": 1200,\"parcelas\": 10,\"seguro\": true}")
		.when()
			.post("simulacoes")
		.then()
			.statusCode(201)
			.extract().path("id");
	}
	
	@Test
	public void t03_erroRegra() {
		given()
			.body("{ \"nome\": \""+CONTA_NAME+"\", \"cpf\": "+CONTA_CPF+",\"email\": \"\", \"valor\": 1200,\"parcelas\": 3,\"seguro\": true}")
		.when()
			.post("simulacoes")
		.then()
			.statusCode(400)
			.body("erros.email",is("E-mail deve ser um e-mail válido"));
	}
	
	@Test
	public void t04_cpfExistente() {
		given()
			.body("{ \"nome\": \""+CONTA_NAME+"\", \"cpf\": "+CONTA_CPF+",\"email\": \"email@email.com\", \"valor\": 1200,\"parcelas\": 3,\"seguro\": true}")
		.when()
			.post("/simulacoes")
		.then()
			.statusCode(400)
			.body("mensagem",is("CPF duplicado"));
	}
	
	@Test
	public void t05_alterarSimulacao() {
		given()
			.body("{ \"nome\": \""+CONTA_NAME+" alterada\", \"cpf\": "+CONTA_CPF+",\"email\": \"email@email.com\", \"valor\": 1200,\"parcelas\": 3,\"seguro\": true}")
		.when()
			.put("simulacoes/"+CONTA_CPF+"")
		.then()
			.statusCode(200);
	}
	
	@Test
	public void t06_cpfInexistente() {
		given()
			.body("{ \"nome\": \"Lampard coimba\", \"cpf\": 97093336013,\"email\": \"email@email.com\", \"valor\": 1200,\"parcelas\": 3,\"seguro\": true}")
		.when()
			.put("simulacoes/97093336013")
		.then()
			.statusCode(404)
			.body("mensagem",is("CPF 97093336013 não encontrado"));
	}
	
	@Test
	public void t07_consultarTodasSimulacoes() {
		given()
		.when()
			.get("simulacoes")
		.then()
			.log().all()
			.statusCode(200);
	}
	
	@Test
	public void t08_consultarSimulacao() {
		given()
		.when()
			.get("simulacoes/"+CONTA_CPF+"")
		.then()
			.log().all()
			.statusCode(200);
	}
	
	@Test
	public void t09_consultarSimulacaoNaoCadastrada() {
		given()
		.when()
			.get("simulacoes/97093636013")
		.then()
			.statusCode(404)
			.body("mensagem",is("CPF 97093636013 não encontrado"));
	}
	
	@Test
	public void t10_deletarSimulacao() {
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("simulacoes/{id}")
		.then()
			.statusCode(200);
	}

	}
	
