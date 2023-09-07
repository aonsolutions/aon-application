#!/bin/bash

# if command starts with an option, prepend node insight.js
if [ "${1:0:1}" = '-' ]; then
        set -- java -jar aon-dump.jar "$@"
fi

exec "$@"

