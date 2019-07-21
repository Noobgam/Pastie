## Pastie

Minimalistic site made to share code snippets / small files.

## How to build image

# First step

You need to place secrets in the build folder by the path `./srv/noobgam/pastie/secrets`

# Easy way:

`sudo docker build --network=host -t pastie-backend -f docker/Dockerfile .`

This is ultra-slow, gradle warmup is really bad, requires only docker installed.

# Recommended way:

`gradle build && sudo docker build --network=host -t pastie-backend -f docker/Dockerfile .`

This runs gradle on host machine, then copies jars to the build image. Significantly improves build speed (From my experience the `gradle build` part has `x40`-ish speedup) due to gradle daemon, but technically unsafe.
