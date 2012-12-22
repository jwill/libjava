PKGS = \
    j/io \
    j/util

# File name of final jar file
JAR_FILE_NAME = libjava.jar

##### Do not edit beyond this line #####

SRCS = $(addsuffix /*.java,$(addprefix src/,$(PKGS)))

OUT_DIR = bin

### targets
all: printFlags $(JAR_FILE_NAME)

### rules
printFlags:
	@mkdir -p "$(OUT_DIR)"
	@echo "Compiling using: javac\n"

$(JAR_FILE_NAME): $(SRCS)
	javac -d "$(OUT_DIR)" $^
	jar -cfv "$@" -C "$(OUT_DIR)" .

clean:
	rm -rf "$(OUT_DIR)" "$(JAR_FILE_NAME)" *~ 

