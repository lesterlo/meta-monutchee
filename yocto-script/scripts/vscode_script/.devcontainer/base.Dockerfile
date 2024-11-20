FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN ./install_lib.sh && ./setup_env.sh 


# Set the default shell to bash instead of sh
ENV SHELL /bin/zsh