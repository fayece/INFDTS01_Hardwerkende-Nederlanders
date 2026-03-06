sp:
	mvn spotless:apply

ja:
	mvn clean jacoco:prepare-agent test jacoco:report jacoco:check

qo:
	docker run --rm \
	  -v $(CURDIR):/data/project \
      -v $(CURDIR)/qodana-results:/data/results \
      jetbrains/qodana-jvm-community:2025.2