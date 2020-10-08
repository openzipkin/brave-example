# docker rationale

## Why do we set `--build-arg target` to the same value as `--target`?

There are very few differences in our docker images from a scripting point of
view. However, each has different contents, and most have a different base
layer. We need to know the target stage before we create it, so that we can
build the right contents. There's no known way to read the value of the
`--target` parameter. Hence, we duplicate it as a build argument.
