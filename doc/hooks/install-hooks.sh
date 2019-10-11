#!/bin/sh

cd "$(dirname "$0")/../.." || exit

rm -rf .git/hooks

ln -s ../doc/hooks .git/hooks
sudo chmod -R 777 doc/hooks/*
