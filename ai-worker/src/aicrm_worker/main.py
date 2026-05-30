from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="AICRM AI Worker", version="0.1.0")


class IngestRequest(BaseModel):
    document_id: int
    source_uri: str


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/documents/ingest")
def ingest_document(request: IngestRequest) -> dict[str, str | int]:
    return {
        "document_id": request.document_id,
        "status": "queued",
        "source_uri": request.source_uri,
    }
