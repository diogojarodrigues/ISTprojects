#ifndef L2CACHE_H
#define L2CACHE_H

#define DEBUG 0
#define L1_NUM_BLOCKS (L1_SIZE / BLOCK_SIZE)
#define L2_NUM_BLOCKS (L2_SIZE / BLOCK_SIZE)

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../Cache.h"

void resetTime();

uint32_t getTime();

/****************  RAM memory (byte addressable) ***************/
void accessDRAM(uint32_t, uint8_t *, uint32_t);

/*********************** Cache *************************/

void initCache();
void destroyCache();
void accessL1(uint32_t, uint8_t *, uint32_t);
void accessL2(uint32_t, uint8_t *, uint32_t);
int indexL1(uint32_t address);
int indexL2(uint32_t address);

typedef struct CacheLine {
    uint8_t valid;
    uint8_t dirty;
    uint32_t tag;
    uint8_t block[BLOCK_SIZE];
} CacheLine;

typedef struct Cache {
    uint32_t init;
    CacheLine* lines;
} Cache;

/*********************** Interfaces *************************/

void read(uint32_t, uint8_t *);

void write(uint32_t, uint8_t *);

/* Debugging */

/* prints L1 contents (only non-empty addresses) */
void printL1();
void printL2();
void printDRAM();
void printAll();
void debug(char *);

/* Variables */
#endif
