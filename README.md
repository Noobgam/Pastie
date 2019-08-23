# Pastie

Minimalistic [site](https://paste.noobgam.me/) made to share code snippets / small files.

Frontend link: https://github.com/Noobgam/Pastie-frontend

# How to build image

## First step

You need to place secrets in the build folder by the path `./srv/noobgam/pastie/secrets`

## Easy way:

`sudo docker build --network=host -t pastie-backend -f docker/Dockerfile-raw .`

This is ultra-slow, gradle warmup is really bad, requires only docker installed.

## Recommended way:

`gradle build && sudo docker build --network=host -t pastie-backend -f docker/Dockerfile .`

This runs gradle on host machine, then copies jars to the build image. Significantly improves build speed (From my experience the `gradle build` part has `x40`-ish speedup) due to gradle daemon, but technically unsafe.

## Quick script to initialize a node (ubuntu/bionic) with docker and awscli

```bash
sudo apt-get update && \
sudo apt-get install \
    python3 \
    python3-pip \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common -y && \
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - && \
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable" && \
sudo apt-get update && \
sudo apt-get install docker-ce docker-ce-cli containerd.io -y && \
pip3 install awscli --upgrade --user && \
echo 'PATH=$PATH:~/.local/bin' >> ~/.bashrc && \
source ~/.bashrc
```
