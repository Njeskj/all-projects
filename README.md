# All Projects

Quatro plataformas backend independentes, construídas com a mesma stack para explorar
diferentes domínios: **Go + Spring Boot + gRPC + PostgreSQL + Kafka + Redis + Kubernetes +
Terraform + ArgoCD + OpenTelemetry**.

Cada projeto vive na sua própria pasta neste repositório, com deploy real verificado num
cluster Kubernetes local (kind) via GitOps (ArgoCD).

| Projeto | Pasta | Domínio |
|---|---|---|
| [Booking Platform](./booking-platform) | `booking-platform/` | Reservas/ticketing — disponibilidade de vagas com lock distribuído contra overbooking |
| [E-commerce Platform](./ecommerce-platform) | `ecommerce-platform/` | Catálogo, pedidos e estoque |
| [Observability Platform](./observability-platform) | `observability-platform/` | Plataforma de observabilidade própria — ingestão de métricas e gestão de dashboards |
| [Matching Platform](./matching-platform) | `matching-platform/` | Matching/leilão em tempo real — motor de casamento de ofertas |

## Arquitetura comum

Cada projeto segue o mesmo desenho:

- **Serviço Go**: hot path de alta performance (disponibilidade, estoque, ingestão de métricas
  ou motor de matching, conforme o domínio).
- **Serviço Spring Boot**: gestão de domínio, CRUD, integrações (pagamentos, pedidos, regras,
  usuários).
- **gRPC**: contrato entre os dois serviços, definido em `.proto`.
- **PostgreSQL**: persistência de cada serviço, migrações via Flyway/goose.
- **Redis**: cache ou estrutura de dados específica do domínio (lock distribuído, cache de
  estoque, sorted set de ofertas).
- **Kafka**: um evento de domínio publicado por um serviço e consumido pelo outro.
- **OpenTelemetry**: tracing do fluxo gRPC ponta-a-ponta.
- **Docker + Kubernetes**: imagens publicadas num registry local, manifests aplicados num
  namespace dedicado por projeto num cluster kind compartilhado.
- **Terraform**: gerencia recursos Kubernetes do namespace do projeto (ConfigMap/Secret),
  evitando conflito de dual-writer com os manifests aplicados via ArgoCD.
- **ArgoCD**: sincroniza cada projeto a partir do seu próprio path `k8s/`, com uma Application
  dedicada por projeto.

## Como navegar

Cada pasta de projeto contém o código completo, `docker-compose.yml` para rodar localmente,
manifests `k8s/`, configuração `terraform/`, e o `status.json` documentando a verificação de
cada fase da construção (build, testes de integração, deploy).
