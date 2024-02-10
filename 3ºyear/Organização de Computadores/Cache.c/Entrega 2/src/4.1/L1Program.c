#include "L1Cache.h"

void test1() {
    uint8_t a[4], b[4] = {0, 2, 0, 0}, c[4] = {5, 6, 7, 8};
    debug("BEGINNING");
    printAll();

    // 1010 0000 0000 0000
    debug("\nwrite(0xA000, b);");
    write(0xA000, b);
    printAll();

    debug("\nwrite(0xE000, c);");
    write(0xE000, c);
    printAll();

    debug("\nwrite(0xA000, b);");
    write(0xA000, b);
    printAll();

    debug("\nread(0xE002, &a);");
    read(0xE002, a);
    printf("\na = %d %d %d %d\n", a[0], a[1], a[2], a[3]);
    printAll();
}

int main() {
    initCache();

    test1();

    destroyCache();
    return 0;
}

