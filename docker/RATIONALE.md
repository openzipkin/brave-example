# docker rationale

## Why does `build_image` manually parse XML?

`build_image` is a script used to build Docker images per project. We need
some details from the pom file for build arguments. Rather than require
installing an XML parser or invoking Maven, this uses `sed` which is commonly
present on developer laptops and CI nodes.
