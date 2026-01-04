#!/bin/sh
# 1. 서버 시작
vault server -config=/vault/config/config.hcl > /vault/data/server.log 2>&1 &

echo "==> Waiting for Vault..."
until vault status -address=http://127.0.0.1:8200 2>&1 | grep -q "Initialized"; do
    sleep 1
done

export VAULT_ADDR='http://127.0.0.1:8200'

# 2. 초기화 (데이터 없으면 수행)
if [ ! -f /vault/data/keys.txt ]; then
    vault operator init -key-shares=1 -key-threshold=1 > /vault/data/keys.txt
    TEMP_TOKEN=$(grep "Initial Root Token:" /vault/data/keys.txt | cut -d' ' -f4 | tr -d '\r')
    K1=$(grep "Unseal Key 1:" /vault/data/keys.txt | cut -d' ' -f4 | tr -d '\r')
    
    vault operator unseal "$K1"
    
    # Root 토큰을 'myroot'로 고정
    VAULT_TOKEN="$TEMP_TOKEN" vault token create -id="myroot" -policy="root"
    export VAULT_TOKEN="myroot"

    # [중요] secret 엔진을 KV Version 2로 활성화 (403 에러 방지 핵심)
    # 이미 존재하면 무시, 없으면 kv-v2로 생성
    vault secrets enable -path=secret kv-v2 || true
fi

# 상시 Unseal
K1=$(grep "Unseal Key 1:" /vault/data/keys.txt | cut -d' ' -f4 | tr -d '\r')
vault operator unseal "$K1"

export VAULT_TOKEN="myroot"
sleep 1

# 3. KV V2 방식으로 데이터 주입 (kv put 사용)
echo "==> Injecting secrets into KV V2..."

echo "==> application 데이터 저장"
vault kv put secret/application @/vault/data/application.json || true

echo "==> application/dev 데이터 저장"
vault kv put secret/application/dev @/vault/data/application_dev.json || true

echo "==> default 데이터 저장"
vault kv put secret/licensing-service @/vault/data/default.json || true

echo "==> dev 데이터 저장"
vault kv put secret/licensing-service/dev @/vault/data/dev.json || true

echo "==> prod 데이터 저장"
vault kv put secret/licensing-service/prod @/vault/data/prod.json || true



echo "==> VAULT READY (Token: myroot, Engine: KV-V2)"
wait