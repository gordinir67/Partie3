# Chatop API

API REST pour la gestion de locations immobilières développée avec **Spring Boot**, **MySQL** et sécurisée par **JWT**.  
Documentation interactive disponible via **Swagger (OpenAPI)**.

---

## Prérequis

- **Java 17**
- **Maven 3.9.12**
- **MySQL 8+**
- **Spring Boot 3.3.6**

---

##  Démarrage

### 1. Cloner le projet
```bash
git clone https://github.com/gordinir67/Partie3.git
```

### 2. Créer la base MySQL
```sql
CREATE DATABASE chatop_db;
```
Utiliser ensuite le script fourni.

### 3. Configurer les variables d’environnement

| Variable | Description |
|---------|------------|
| `P3_DB_USERNAME` | Utilisateur MySQL |
| `P3_DB_PASSWORD` | Mot de passe MySQL |
| `JWT_SECRET` | Clé secrète JWT (256 bits min) |

Générer clé secrète (sous Windows):
```bash
[Convert]::ToBase64String((1..64 | % {Get-Random -Maximum 256}))
```

### 4. Lancer l’API

```bash
mvn spring-boot:run
```

API disponible sur :
```
http://localhost:3001
```

---

## Swagger

Documentation interactive :
```
http://localhost:3001/swagger-ui/index.html
```

