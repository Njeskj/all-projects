#!/bin/bash
# usage: update_status.sh <phase> <phase_index> <status> <message> [error]
set -e
F=/z/laragon/www/eu/observability-platform/status.json
PHASE="$1"; IDX="$2"; STATUS="$3"; MSG="$4"; ERR="${5:-null}"
TS=$(date -u +%Y-%m-%dT%H:%M:%SZ)
PCT=$(( IDX * 100 / 12 ))
python3 - "$F" "$PHASE" "$IDX" "$STATUS" "$TS" "$PCT" "$MSG" "$ERR" <<'PYEOF'
import json,sys
f,phase,idx,status,ts,pct,msg,err=sys.argv[1:9]
try:
    with open(f) as fh: d=json.load(fh)
except Exception:
    d={"log":[]}
d["project"]="observability-platform"
d["phase"]=phase
d["phase_index"]=int(idx)
d["total_phases"]=12
d["percent"]=int(pct)
d["status"]=status
d["last_update"]=ts
d.setdefault("log",[])
d["log"].append(f"{ts} {msg}")
d["log"]=d["log"][-15:]
d["error"]=None if err=="null" else err
with open(f,"w") as fh: json.dump(d,fh,indent=2)
PYEOF
