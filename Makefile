PKGS = \
    j/io \
    j/util

# File name of final jar file
JAR_FILE_NAME = libjava.jar

##### Do not edit beyond this line #####

CLASSES = $(addsuffix /*.class,$(addprefix $(OUT_DIR)/,$(PKGS)))

OUT_DIR = bin

### targets
all: printFlags $(JAR_FILE_NAME)

### rules
printFlags:
	@mkdir -p "$(OUT_DIR)"
	@echo "Compiling using: javac\n"

$(OUT_DIR)/%.class: src/%.java
	javac -cp "$(OUT_DIR)" -source 1.5 -target 1.5 -d "$(OUT_DIR)" $?

$(JAR_FILE_NAME): $(CLASSES)
	jar -cfv "$@" -C "$(OUT_DIR)" .

clean:
	rm -rf "$(OUT_DIR)" "$(JAR_FILE_NAME)" *~ 

