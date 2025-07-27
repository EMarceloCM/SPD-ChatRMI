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
javac -d . src/server/*.java src/client/*.java
```

3. **Rode o servidor** 
```bash
java -cp . server.ChatServer
``` 

4. **Rode o cliente**  
```bash
java -cp . client.ChatUI
```

### Nota
#### Para utilizar um nome fixo de host ao invés do ip da máquina, é necessário seguir os seguintes passos:

1. **Editar o arquivo hosts em cada máquina (servidor e clientes)**
```bash
Windows: C:\Windows\System32\drivers\etc\hosts
Linux/macOS: /etc/hosts
```
Adicione uma linha apontando o nome fixo para o IP atual do servidor, por exemplo:
```bash
192.168.0.105   chat.local
```

2. **No servidor, configure o RMI para usar esse nome**
```bash
java -Djava.rmi.server.hostname=chat.local -cp bin server.ChatServer
```
No seu código, registre com:
```bash
Naming.rebind("rmi://chat.local/GroupChatService", servidor);
```

3. **No(s) cliente(s), faça o lookup usando chat.local (ou o nome que tiver dado)**
```bash
IChatServer srv = (IChatServer) Naming.lookup("rmi://chat.local/GroupChatService");
```
### Requisitos

1. ## Rede local estável e endereço fixo para o servidor
2. ## Login e senha corporativos para evitar usuários anônimos
3. ## Criptografia de tráfego: Use SSL/TLS nos sockets RMI (via SslRMIClientSocketFactory e SslRMIServerSocketFactory) para proteger mensagens em trânsito.
4. ## Autorização e permissões
5. ## Proteção contra ataques internos
6. ## Persistencia de mensagens
7. ## Servidor redundante: Mecanismo de failover (servidor secundário ou cluster) para não ficar offline em caso de falha.
8. ## Suporte a chat privado
9. ## Suporte a envio de arquivos