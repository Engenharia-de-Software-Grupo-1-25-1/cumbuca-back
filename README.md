<p align="center">
  <img src="https://raw.githubusercontent.com/Engenharia-de-Software-Grupo-1-25-1/cumbuca-front/main/src/assets/logo.svg" alt="Logo Cumbuca" width="160"/>
</p>

<h1 align="center">Cumbuca</h1>


## Conteúdos
- [Descrição](#descrição)
- [Preview das Telas](#preview-das-telas)
  - [Feed](#-feed)
  - [Meu Perfil](#-meu-perfil)
  - [Feed de Estabelecimentos](#-feed-de-estabelecimentos)
  - [Gerenciamento de Conta e Avaliações](#-gerenciamento-de-conta-e-avaliações)
- [Guia de Instalação](#-guia-de-instalação)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Equipe / Autores](#equipe--autores)

<h2 id="descricao">📝Descrição</h2>

O **Cumbuca** é uma plataforma web criada para centralizar e organizar informações sobre bares, restaurantes e estabelecimentos gastronômicos de Campina Grande (PB) e região.  

Atualmente, moradores e turistas enfrentam dificuldades para encontrar dados confiáveis e atualizados em fontes como redes sociais e Google Maps, que muitas vezes são incompletos e dispersos. Da mesma forma, empreendedores locais encontram poucos meios eficazes de divulgar seus serviços, promoções e diferenciais.

O Cumbuca surge para resolver esse problema, reunindo em um só lugar avaliações feitas pelos próprios usuários, notas de comida, ambiente e atendimento, além de comentários, curtidas e filtros avançados.  

Com isso, a plataforma:  
- Facilita a escolha de onde comer, beber ou se reunir com amigos.  
- Valoriza a cena gastronômica local e fortalece a visibilidade dos empreendimentos.  
- Incentiva o consumo consciente e de qualidade.  
- Contribui para o desenvolvimento econômico e cultural da cidade.  

Em resumo, o Cumbuca conecta pessoas aos melhores lugares para viver experiências gastronômicas, ao mesmo tempo em que apoia o crescimento de negócios locais.

<h2 id="preview-das-telas">📸 Preview das Telas</h2>

### 📰 Feed
O **Feed** funciona como a linha do tempo principal da plataforma, reunindo as avaliações mais recentes feitas pelos usuários sobre diferentes restaurantes.  
Nele é possível **visualizar notas e comentários**, além de **curtir** e **interagir** com as publicações de outros membros da comunidade.
<p align="center">
  <img width="750" alt="Feed" src="https://github.com/user-attachments/assets/61d2656b-1948-4d0c-afd0-b11b5ba709f8" />
</p>

### 👤 Meu Perfil
A tela **Meu Perfil** reúne todas as informações do usuário, como nome, foto e dados de conta.  
Nela também estão listadas todas as avaliações já feitas, permitindo visualizar, editar ou excluir cada uma delas.
<p align="center">
  <img width="750" alt="image" src="https://github.com/user-attachments/assets/62fbc826-7209-4e52-baa1-824534f5ee60" />
</p>

### 🍲 Feed de Estabelecimentos
O **Feed de Estabelecimentos** exibe todos os restaurantes cadastrados na plataforma.  
Ao clicar em um estabelecimento, o usuário tem acesso a todas as avaliações feitas por outros usuários, com notas de comida, ambiente e atendimento, além de curtidas e comentários.

<p align="center">
  <img width="750" alt="Feed de Estabelecimentos" src="https://github.com/user-attachments/assets/0f867764-3b7e-4ffd-a430-036a56da4e17" />
</p>

### 👥 Gerenciamento de Conta e Avaliações
Nesta seção estão reunidas algumas das principais ações do usuário dentro da plataforma.  
É possível **criar uma conta** para acessar o sistema, **editar informações do perfil** a qualquer momento e **realizar avaliações de restaurantes**, atribuindo notas e comentários.  
Além disso, o usuário pode **visualizar os detalhes de cada avaliação**, **comentar** em avaliações de outros usuários e também **editar ou excluir** suas próprias avaliações quando necessário.

<p align="center">
  <img src="https://github.com/user-attachments/assets/d405f974-325d-4d05-8c8a-5fff238f22e9" alt="Tela de Login" width="350"/>
  <img src="https://github.com/user-attachments/assets/edc34804-0eb7-45eb-b926-984f045f7644" alt="Tela de Cadastro" width="350"/>
  <img src="https://github.com/user-attachments/assets/a37d4206-02bc-4172-98a5-3ab59f2c313e" alt="Gerenciamento de Conta" width="350"/>
  <img src="https://github.com/user-attachments/assets/cc5ec1b5-2e0b-4a51-8c12-928445e554c0" alt="Comentar avaliação" width="350"/>
</p>

## 🔧 Guia de Instalação

### 1. Instalar o SDKMAN!

O SDKMAN! permite instalar e alternar facilmente entre versões do Java.

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

### 2. Instalar o Java 21 (versão utilizada pelo projeto)

```bash
sdk install java 21.0.7-ms
sdk use java 21.0.7-ms
```

---

### ▶️ Executando a Aplicação

Após configurar o Java, execute o backend com o Gradle Wrapper incluso no projeto:

```bash
./gradlew bootRun
```

---

### 🧪 Executando os Testes e Verificações

### Testes Automatizados

```bash
./gradlew test
```

### Build com limpeza de arquivos anteriores

```bash
./gradlew clean build
```

### Pipeline completa com validações (build, testes, checkstyle, etc)

```bash
./gradlew clean check
```

<h2 id="tecnologias-utilizadas">💻 Tecnologias Utilizadas</h2>

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)

<h2 id="equipe--autores">🤝 Equipe / Autores</h2>

- [Luciano Nascimento](https://github.com/LucianoLN)
- [Márcia Thaís](https://github.com/marciathais)
- [Maria Helena Barros](https://github.com/helenazbm)
- [Rayanne Bento](https://github.com/rayanne-on)
- [Thawany Fernandes](https://github.com/Thawanyfernandes)
