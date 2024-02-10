#include "L1Cache.h"

Cache L1;
uint8_t DRAM[DRAM_SIZE];

uint32_t time;

void debug(char *msg) {
    if (DEBUG) {
        printf("%s\n", msg);
    }
}

/**************** Time Manipulation ***************/
void resetTime() { time = 0; }

uint32_t getTime() { return time; }

/****************  RAM memory (byte addressable) ***************/
void accessDRAM(uint32_t address, uint8_t *data, uint32_t mode) {
    if (address >= DRAM_SIZE - WORD_SIZE + 1) exit(-1);

    if (mode == MODE_READ) {
        memcpy(data, &(DRAM[address]), BLOCK_SIZE);
        time += DRAM_READ_TIME;
    }

    if (mode == MODE_WRITE) {
        memcpy(&(DRAM[address]), data, BLOCK_SIZE);
        time += DRAM_WRITE_TIME;
    }
}

/*********************** L1 cache *************************/

void initCache() {
    /* create all lines */
    L1.lines = (CacheLine *)malloc(L1_NUM_BLOCKS * sizeof(CacheLine));

    /* init all values to 0 */
    for (int i = 0; i < L1_NUM_BLOCKS; i++) {
        L1.lines[i].dirty = 0;
        L1.lines[i].valid = 0;
        L1.lines[i].tag = 0;
        for (int k = 0; k < BLOCK_SIZE; k++) {
            L1.lines[i].block[k] = 0;
        }
    }
    L1.init = 1;

    debug("L1 cache initiated.");
}

void destroyCache() {
    free(L1.lines);
    L1.init = 0;
}

int indexL1(uint32_t address) {
    return (address / BLOCK_SIZE) % (L1_NUM_BLOCKS);
}

void accessL1(uint32_t address, uint8_t *data, uint32_t mode) {
    uint32_t index, tag, offset, dirty_address;

    if (L1.init == 0) {
        initCache();
    }

    tag = address / L1_SIZE;
    index = indexL1(address);
    offset = address % BLOCK_SIZE;
    CacheLine *l1_line = &L1.lines[index];

    // /* debug */
    // char temp_str[128];
    // sprintf(temp_str, "L1: 0x%06X | Tag: 0x%X, Index: %d, Offset: %d, Mode: %s",
    //         address, tag, index, offset, mode ? "READ" : "WRITE");
    // debug(temp_str);

    if (!(l1_line->valid && l1_line->tag == tag)) {  // L1 Miss
        debug("L1 miss");

        // line has already a dirty block with different tag
        if ((l1_line->valid) && (l1_line->dirty)) {  
            debug("L1 block dirty");

            dirty_address = l1_line->tag * L1_SIZE + index * BLOCK_SIZE;
            accessDRAM(dirty_address, l1_line->block, MODE_WRITE);          // then write back old block
        }

        /* get block from L2 */
        accessDRAM(address - offset, l1_line->block, MODE_READ);
        l1_line->valid = 1;
        l1_line->tag = tag;
        l1_line->dirty = 0;
    }

    if (mode == MODE_READ) {  // read data from cache line
        memcpy(data, &l1_line->block[WORD_SIZE * (offset / WORD_SIZE)], WORD_SIZE);
        time += L1_READ_TIME;
    }

    if (mode == MODE_WRITE) {  // write data to cache line
        memcpy(&l1_line->block[WORD_SIZE * (offset / WORD_SIZE)], data, WORD_SIZE);
        l1_line->dirty = 1;
        time += L1_WRITE_TIME;
    }
}

void read(uint32_t address, uint8_t *data) {
    accessL1(address, data, MODE_READ);
}

void write(uint32_t address, uint8_t *data) {
    accessL1(address, data, MODE_WRITE);
}

/* prints L1 contents (only non-empty addresses) */
void printL1() {
    uint8_t print = 0;
    printf("\n----L1----\n");
    for (int i = 0; i < L1_NUM_BLOCKS; i++) {       // for each index
        for (int k = 0; k < BLOCK_SIZE; k += WORD_SIZE) {  // for each word
            for (int j = 0; j < WORD_SIZE;
                 j++) {  // for each byte, check if any is non-null
                if (L1.lines[i].block[k + j]) {
                    print = 1;
                }
            }
            if (print) {  // if so, print the word
                for (int l = 0; l < WORD_SIZE; l++) {
                    printf("0x%X | %d\n", i * BLOCK_SIZE + k + l,
                           L1.lines[i].block[k + l]);
                }
            }

            print = 0;
        }
    }
    printf("----------\n");
}

/** Prints DRAM contents (only non-empty blocks) */
void printDRAM() {
    printf("\n---DRAM---\n");
    for (int i = 0; i < DRAM_SIZE; i++) {
        if (DRAM[i]) {
            printf("0x%X | %d\n", i, DRAM[i]);
        }
    }
    printf("----------\n");
}

/** Prints all memory */
void printAll() {
    printL1();
    printDRAM();
}

