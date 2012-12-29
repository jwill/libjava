# Makefile template for Java.
# The following vars are available for customizing the build process:
#
# PKGS [Required] :
#   Space-separated names of packages to compile. 
#   For e.g., PKGS = com/pkg/util com/pkg/io
#   The packages are assumed to be in the src dir.
#
# JAR_FILE_NAME [Required] :
#   Name of the final jar file.
#   For e.g., JAR_FILE_NAME = mylib.jar
#
# LIBS [Default=none] :
#   Relative paths (from Makefile) to external jar files.
#   For e.g., LIBS = logback-1.0/logback.jar libxml/libxml.jar
#
# MAIN_CLASS [Default=none] :
#   Name of main class along with full package path.
#   For e.g., MAIN_CLASS = com.pkg.App
#
# TARGET_VERSION [Default=1.5] :
#   Target JVM version to compile for. 
#   For e.g., TARGET_VERSION = 1.5
#
# Output vars (created by this template):
# SRCS :
#   Space-separated paths to each individual .java file 
#   For e.g., SRCS = src/com/pkg/Util.java src/com/pkg/InputStream.java
#
# CLASSES :
#   Space-separated paths to each individual compiled .class file 
#   For e.g., CLASSES = bin/com/pkg/Util.class bin/com/pkg/InputStream.class
#
# JAVAC_OPTS : all command-line options to javac
# JAVADOC_OPTS : all command-line options to javadoc
#

ifeq ($(strip $(TARGET_VERSION)),)
    TARGET_VERSION = 1.5
endif

SRC_DIR = src
OUT_DIR = bin
DOCS_DIR = docs
PATH_SEP = :
DIR_SEP = / # unused for now since Windows can handle / ?

# TMP1_ = src/j/io/*.java etc....
TMP1_ = $(addsuffix /*.java,$(addprefix $(SRC_DIR)/,$(PKGS)))

# Expand the wildcards to get the actual file names
SRCS = $(foreach dir,$(TMP1_),$(wildcard $(dir)))

TMP2_ = $(SRCS:.java=.class)
CLASSES = $(TMP2_:$(SRC_DIR)/%=$(OUT_DIR)/%)

TMP3_ = $(addsuffix $(PATH_SEP),$(LIBS) $(SRC_DIR)/ $(OUT_DIR)/)
CLASSPATHS = $(subst $(PATH_SEP) ,$(PATH_SEP),$(TMP3_))
LIBS_OPTS = -cp "$(CLASSPATHS)"

LIBS_TARGETS = $(addprefix $(OUT_DIR)/,$(LIBS:.jar=.xxx))

ifeq ($(strip $(MAIN_CLASS)),)
    JAR_OPTS = cf
else
    JAR_OPTS = cfe
endif

JAVAC_OPTS = $(LIBS_OPTS) -source $(TARGET_VERSION) \
    -target $(TARGET_VERSION) -d "$(OUT_DIR)"

PKGS_PROPER = $(subst /,.,$(PKGS))

JAVADOC_OPTS = -source $(TARGET_VERSION) -sourcepath "$(SRC_DIR)/" \
    -protected -charset UTF-8 -classpath "$(CLASSPATHS)" \
    -keywords -d "$(DOCS_DIR)" $(PKGS_PROPER)

###
### targets
###
.PHONY: all docs printFlags clean

all: printFlags $(JAR_FILE_NAME)

docs: $(SRCS)
	@mkdir -p "$(DOCS_DIR)"
	javadoc $(JAVADOC_OPTS)

printFlags:
	@mkdir -p "$(OUT_DIR)"
	@echo "Compiling using: javac $(JAVAC_OPTS)"

clean:
	rm -rf "$(OUT_DIR)" "$(JAR_FILE_NAME)" *~ 

###
### rules
###

### This indicates do not remove intermediate files
.SECONDARY:

### This expands the external jar files into bin for later combination
$(OUT_DIR)/%.xxx: %.jar  
	@mkdir -p "$(dir $(OUT_DIR)/$*.xxx)"
	@echo -n > "$@"
	cd "$(OUT_DIR)"; jar -xf "../$<"

$(OUT_DIR)/%.class: $(SRC_DIR)/%.java $(LIBS_TARGETS)
	@echo "Compiling \"$<\" ..."
	@javac $(JAVAC_OPTS) $< 
# $(?:src/%=%)

$(JAR_FILE_NAME): $(CLASSES) 
	jar $(JAR_OPTS) "$@" $(MAIN_CLASS) -C "$(OUT_DIR)" .

