# template make file for java
# The following vars must be predefined:
#
# PKGS = list of packages. for e.g., java/util  java/net
#        The packages are assumed to be in the src dir
#
# LIBS = relative paths to external jar files
#
# JAR_FILE_NAME = name of final jar file
#
# MAIN_CLASS = name of main class along with full package 
#              path. For e.g., my.pkg.App
#              Leave blank if don't have.


OUT_DIR = bin

# TMP1_ = src/j/io/*.java etc....
TMP1_ = $(addsuffix /*.java,$(addprefix src/,$(PKGS)))

# Expand the wildcards to get the actual file names
SRCS = $(foreach dir,$(TMP1_),$(wildcard $(dir)))

TMP2_ = $(SRCS:.java=.class)
CLASSES = $(TMP2_:src/%=$(OUT_DIR)/%)

TMP3_ = $(addsuffix :,$(LIBS) src/ bin/)
LIBS_OPTS = -cp "$(subst : ,:,$(TMP3_))"

LIBS_TARGETS = $(addprefix $(OUT_DIR)/,$(LIBS:.jar=.xxx))

ifeq ($(strip $(MAIN_CLASS)),)
    JAR_OPTS = cf
else
    JAR_OPTS = cfe
endif

JAVAC_OPTS = $(LIBS_OPTS) -source 1.5 -target 1.5 -d "$(OUT_DIR)"

### targets
all: printFlags $(JAR_FILE_NAME)

### rules
printFlags:
	@mkdir -p "$(OUT_DIR)"
	@echo "Compiling using: javac $(JAVAC_OPTS)"

### This indicates do not remove intermediate files
.SECONDARY:

$(OUT_DIR)/%.xxx: %.jar  
	@mkdir -p "$(dir $(OUT_DIR)/$*.xxx)"
	@echo -n > "$@"
	cd "$(OUT_DIR)"; jar -xf "../$<"

$(OUT_DIR)/%.class: src/%.java $(LIBS_TARGETS)
	@echo "Compiling \"$<\" ..."
	@javac $(JAVAC_OPTS) $< 
# $(?:src/%=%)

$(JAR_FILE_NAME): $(CLASSES) 
	jar $(JAR_OPTS) "$@" $(MAIN_CLASS) -C "$(OUT_DIR)" .

clean:
	rm -rf "$(OUT_DIR)" "$(JAR_FILE_NAME)" *~ 

