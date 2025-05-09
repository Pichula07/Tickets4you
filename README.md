# Event4You & Ticket4You API

## Visão Geral

Esta aplicação consiste em dois microsserviços:

- **Event4You**: Responsável pela criação e gerenciamento de eventos.
- **Ticket4You**: Responsável pela geração e gerenciamento de tickets para eventos.

A aplicação utiliza **MongoDB** como banco de dados e está hospedada na **AWS EC2**, acessível pelo IP **3.138.32.175**.

OBSERVAÇÃO = No momento, a instância EC2 foi derrubada por questões financeiras, mas a api funciona perfeitamente de forma local também.
---

## Tecnologias Utilizadas

- **Java Spring Boot**
- **MongoDB**
- **Docker**
- **Feign Client** (para comunicação entre microsserviços)
- **AWS EC2** (hospedagem dos microsserviços)

---

## Como Acessar a API

### Ticket4You Endpoints (`http://3.138.32.175:8082/api/tickets/v1`)

| Método     | Endpoint                      | Descrição                            |
| ---------- | ----------------------------- | ------------------------------------ |
| **GET**    | `/`                           | Retorna todos os tickets             |
| **POST**   | `/create-ticket`              | Cria um novo ticket                  |
| **GET**    | `/{TicketId}`                 | Retorna um ticket pelo ID            |
| **GET**    | `/check-tickets/{EVENT-ID}`   | Verifica se um evento possui tickets |
| **DELETE** | `/delete-ticket/{TICKET-ID}`  | Deleta um ticket pelo ID             |
| **GET**    | `/get-ticket-by-cpf/{CPF}`    | Retorna tickets pelo CPF             |
| **DELETE** | `/delete-ticket-by-cpf/{CPF}` | Deleta tickets pelo CPF              |

### Event4You Endpoints (`http://3.138.32.175:8081/api/events/v1`)

| Método     | Endpoint                   | Descrição                           |
| ---------- | -------------------------- | ----------------------------------- |
| **GET**    | `/`                        | Retorna todos os eventos            |
| **POST**   | `/create-event`            | Cria um novo evento                 |
| **GET**    | `/{EVENT-ID}`              | Retorna um evento pelo ID           |
| **PUT**    | `/{ID_EVENT}`              | Atualiza um evento pelo ID          |
| **DELETE** | `/delete-event/{ID_EVENT}` | Deleta um evento pelo ID            |
| **GET**    | `/sorted`                  | Retorna eventos em ordem alfabética |

---

## Como Utilizar

1. **Baixe o Postman ou Insomnia** para fazer requisições.
2. Utilize os endpoints listados acima para interagir com os microsserviços.
3. Faça requisições em formato JSON para criação e atualização de eventos/tickets.

---

## Exemplo de Requisição

### Criando um event

**POST** `http://3.138.32.175:8082/api/events/v1/create-event`

```json
Entrada:
{
  "eventName": "Teste nome de evento",
  "dateTime": "2024-12-31T21:00:00",
  "zipCode": "99704394"
}
Observação : O zip code puxa automáticamente o endereço completo do código postal

SAIDA:
{
	"id": "67d48bf266d5473308ba3103",
	"eventName": "Festa do github",
	"dateTime": "2024-12-31T21:00:00",
	"zipCode": "99704394",
	"street": "Rua do Brasil",
	"neighborhood": "Bairro do Teste",
	"city": "Erechim",
	"state": "RS"
}
```

---

### Criando um ticket

**POST** `http://3.138.32.175:8082/api/tickets/v1/create-ticket`

```json
Entrada:
  {
  "customerName": "João Pedro Teste",
  "cpf": "57325760027",
  "customerEmail": "joao.silva@example.com",
  "eventId": "67d48bf266d5473308ba3103",
  "brlAmount": "300.00",
  "usdAmount": "55.00"
}
Observação : O eventId deve corresponder a um evento válido, se não retornará um erro.

Saida:
{
	"id": "f6fd4f2c-772d-4c70-9047-12845f1faf7e",
	"customerName": "João Pedro Teste",
	"cpf": "57325760027",
	"customerEmail": "joao.silva@example.com",
	"event": {
		"id": "67d48bf266d5473308ba3103",
		"eventName": "Festa do github",
		"dateTime": "2024-12-31T21:00:00",
		"street": "Rua do Brasil",
		"neighborhood": "Bairro do teste",
		"city": "Erechim",
		"state": "RS"
	},
	"brlTotalAmount": "R$ 300.00",
	"usdTotalAmount": "USD 55.00",
	"status": "concluído"
}
```

---

## Observações

- Certifique-se de que os microsserviços estão rodando na EC2 antes de fazer as requisições.
- Se precisar de suporte, verifique os logs dos microsserviços.

**Desenvolvido por João** 🚀
