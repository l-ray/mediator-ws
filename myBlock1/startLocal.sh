#!/bin/sh

mvn -Dmaven.skip.db.migration=true -Dmaven.cocoon.plugin.phase=compile clean install jetty:run
