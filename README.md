## ⚙️ Configuração do Ambiente

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

## ▶️ Executando a Aplicação

Após configurar o Java, execute o backend com o Gradle Wrapper incluso no projeto:

```bash
./gradlew bootRun
```

---

## 🧪 Executando os Testes e Verificações

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
