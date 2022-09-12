CFILES  = client_handler.c connection.c list.c main.c msg_store.c server.c
HFILES  = client_handler.h connection.h fail.h list.h msg_store.h server.h
OFILES  = $(CFILES:.c=.o)
PROGRAM = msg_server

# -----------------------------------------------------------------------------

CFLAGS  = -std=gnu99 -O2 -g -Wall -Wextra -Werror -Wfatal-errors -Wno-unused-parameter
CC      = gcc
LD      = gcc

# explicit -pthread option not needed on macOS (a.k.a. "Darwin")
# (and results in warnings on some older macOS versions)
ifneq ($(shell uname),Darwin)
  LDFLAGS += -pthread
endif

# -----------------------------------------------------------------------------

.PHONY: all clean
.INTERMEDIATE: $(OFILES)

all: $(PROGRAM)

clean:
	$(RM) $(OFILES) $(PROGRAM)

$(OFILES): $(HFILES)

$(PROGRAM): $(OFILES)
	$(LD) $(OFILES) $(LDFLAGS) -o $@
