# Diplomado En Arquitectura de Software - Trabajo Final k8s

## Grupo: 9
Jaime Alejandro Acero Vargas
Cesar Augusto Bernal Sierra
Alejandra Zuleta
Jhonnier Alberto Sanchez Dorado 
Juan Jose Rojas
David Humberto Morena
Nestor Raúl Camacho

## Repositorios:

| Repositorios |
|--------------|
| 🔗 [Charts & Helm](https://github.com/alejandrazuleta1/pedidos-charts)     |
| 🔗 [Backend](https://github.com/alejandrazuleta1/k8s)          |

## Capitulos:
[1. Arquitectura](#user-content-1-arquitectura)

[2. Instalación del chart manualmente con Helm](#user-content-2-instalación-del-chart-manualmente-con-helm)

[3. Configuración para sincronizar ArgoCD](#user-content-3-configuración-para-sincronizar-argocd)

[4. Endpoints de acceso (backend)](#user-content-4-endpoints-de-acceso-backend)

[5. Demostración en vivo](#user-content-5-demostracion-en-vivo)




## 1. Arquitectura
![alt text](https://github.com/ingalejandroacerov/TestDocker/blob/main/Untitled-2025-08-14-2001.png)


## 2. Instalación del chart manualmente con Helm.
### 2.1 Buscar y reemplazar la versión de la imagen docker

```
sed -i 's/tag: .*/tag: NUMERO_VERSION/' ../values.yaml
```
### 2.2 Insertar la version nueva del los charts

```
yq -i -y '.version |= (split(".") | [.[0], .[1], ((.[2] | tonumber) + 1 | tostring)] | join("."))' ../Chart.yaml
yq '.version' ../Chart.yaml
```

### 2.3 Empaquetamos e instalamos
  
```
helm package my-tech chats/pedido-app 
helm install my-tech charts/pedido-app -n my-tech --create-namespace
```

##  3. Configuración para sincronizar ArgoCD
<img width="1435" height="645" alt="image" src="https://github.com/user-attachments/assets/76fd9ded-a16f-42f3-87db-cd87694bc1e4" />
<img width="1389" height="944" alt="image" src="https://github.com/user-attachments/assets/edf72d0e-4250-4dcb-85c4-82b0450cf7a2" />
<img width="1872" height="928" alt="image" src="https://github.com/user-attachments/assets/efe68b05-263a-41a0-94d9-ba82224cf61b" />


##  4. Endpoints de acceso (backend)
:link: [Ir a PedidoController.java](pedidos-backend/src/main/java/com/pedidos/backend/controller/PedidoController.java)

 ### Comandos cURL para API de Pedidos

### Variables de configuración
```bash
# Configura la URL base de tu API
BASE_URL="http://localhost:8080"
```

### 1. Health Check
```bash
curl -X GET "$BASE_URL/api/pedidos/health"
```

### 2. Obtener todos los pedidos
```bash
curl -X GET "$BASE_URL/api/pedidos" \
  -H "Accept: application/json"
```

### 3. Obtener pedido por ID
```bash
# Reemplaza {id} con el ID del pedido
curl -X GET "$BASE_URL/api/pedidos/1" \
  -H "Accept: application/json"
```

### 4. Crear un nuevo pedido
```bash
curl -X POST "$BASE_URL/api/pedidos" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "cliente": "Juan Pérez",
    "producto": "Laptop Dell",
    "cantidad": 2,
    "precio": 1500.00,
    "estado": "PENDIENTE",
    "direccionEntrega": "Calle 123, Medellín"
  }'
```

### 5. Actualizar un pedido completo
```bash
# Reemplaza {id} con el ID del pedido a actualizar
curl -X PUT "$BASE_URL/api/pedidos/1" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "cliente": "Juan Pérez Actualizado",
    "producto": "Laptop Dell XPS",
    "cantidad": 3,
    "precio": 2000.00,
    "estado": "EN_PROCESO",
    "direccionEntrega": "Calle 456, Medellín"
  }'
```

### 6. Eliminar un pedido
```bash
# Reemplaza {id} con el ID del pedido a eliminar
curl -X DELETE "$BASE_URL/api/pedidos/1" \
  -H "Accept: application/json"
```

### 7. Buscar pedidos por cliente
```bash
curl -X GET "$BASE_URL/api/pedidos/buscar/cliente?nombre=Juan" \
  -H "Accept: application/json"
```

### 8. Buscar pedidos por producto
```bash
curl -X GET "$BASE_URL/api/pedidos/buscar/producto?producto=Laptop" \
  -H "Accept: application/json"
```

### 9. Buscar pedidos por estado
```bash
# Estados comunes: PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO
curl -X GET "$BASE_URL/api/pedidos/estado/PENDIENTE" \
  -H "Accept: application/json"
```

### 10. Obtener pedidos recientes
```bash
curl -X GET "$BASE_URL/api/pedidos/recientes" \
  -H "Accept: application/json"
```

### 11. Cambiar estado de un pedido
```bash
# Reemplaza {id} con el ID del pedido
curl -X PUT "$BASE_URL/api/pedidos/1/estado?estado=COMPLETADO" \
  -H "Accept: application/json"
```

### 12. Obtener estadísticas
```bash
curl -X GET "$BASE_URL/api/pedidos/estadisticas" \
  -H "Accept: application/json"
```

## Script de prueba completo
```bash
#!/bin/bash
BASE_URL="http://localhost:8080"

echo "=== HEALTH CHECK ==="
curl -X GET "$BASE_URL/api/pedidos/health"
echo -e "\n"

echo "=== CREAR PEDIDO ==="
PEDIDO_ID=$(curl -s -X POST "$BASE_URL/api/pedidos" \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Test Cliente",
    "producto": "Test Producto",
    "cantidad": 1,
    "precio": 100.00,
    "estado": "PENDIENTE"
  }' | jq -r '.id')

echo "Pedido creado con ID: $PEDIDO_ID"
echo -e "\n"

echo "=== OBTENER TODOS LOS PEDIDOS ==="
curl -s -X GET "$BASE_URL/api/pedidos" | jq .
echo -e "\n"

echo "=== OBTENER PEDIDO POR ID ==="
curl -s -X GET "$BASE_URL/api/pedidos/$PEDIDO_ID" | jq .
echo -e "\n"

echo "=== ACTUALIZAR ESTADO ==="
curl -s -X PUT "$BASE_URL/api/pedidos/$PEDIDO_ID/estado?estado=COMPLETADO" | jq .
echo -e "\n"
```

##  5. Demostracion en vivo
