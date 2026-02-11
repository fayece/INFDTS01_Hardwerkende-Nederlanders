FROM ubuntu:latest
LABEL authors="Rein"

ENTRYPOINT ["top", "-b"]