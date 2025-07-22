# Sistemas Paralelos Distribuidos - Trabalho Final

## Chat distribuido utilizando Java RMI

## Recursos

- **Mensagens em tempo real**: Converse com outros usuários em tempo real.
- **Mensagens públicas e privadas**: Envie mensagens para todos os usuários ou em privado para um usuário específico.
- **Nicknames de usuário**: Escolha um nickname após realizar a conexão.
- **Entre/Saia do chat**: Conecte ou desconecte do servidor de chat a qualquer momento.
- **Interface gráfica**: Sistema consta com interface intuitiva e de simples utilização.
## Arquitetura

A aplicação segue uma arquitetura cliente-servidor utilizando Java RMI. Aqui está uma breve visão geral:

- **ChatServer**: Gerencia a lógica de mensagens e conexões de clientes.
- **ChatClient**: Liga-se ao servidor e permite a interação do utilizador através de uma interface gráfica.
- **ChatUI**: Fornece a interface gráfica do utilizador para os utilizadores.
- **ConnectedClient**: Gerencia os clientes conectados e facilita o envio de mensagens privadas.

## Instalação

### Prérequisitos

- Java Development Kit (JDK) 8 ou superior

### Passo-a-passo

1. **Clone o repositório**
```bash 
  git clone https://github.com/EMarceloCM/SPD-ChatRMI.git
```
```bash
  cd SPD-ChatRMI
```

2. **Compile o projeto**
```bash
cd chat
cd src
javac -d bin server/*.java client/*.java
```

4. **Rode o servidor** 
```bash
java -cp bin server.ChatServer
``` 

4. **Rode o cliente**  
```bash
java -cp bin client.ChatUI
```
