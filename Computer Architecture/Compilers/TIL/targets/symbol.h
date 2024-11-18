#ifndef __SIMPLE_TARGETS_SYMBOL_H__
#define __SIMPLE_TARGETS_SYMBOL_H__

#include <string>
#include <memory>
#include <cdk/types/basic_type.h>

namespace til {

  class symbol {
    std::shared_ptr<cdk::basic_type> _type;
    std::string _name;
    int _qualifier;
    int _offset = 0; // 0 means global

  public:
    symbol(std::shared_ptr<cdk::basic_type> type, const std::string &name, int qualifier) :
        _type(type), _name(name), _qualifier(qualifier) {
    }

    virtual ~symbol() {
      // EMPTY
    }

    //Getters
    std::shared_ptr<cdk::basic_type> type() const { return _type; }
    const std::string &name() const { return _name; }
    int qualifier() const { return _qualifier; }
    int offset() const { return _offset; }
    bool is_typed(cdk::typename_type name) const { return _type->name() == name; }
    bool global() const { return _offset == 0; }

    //Setters
    int offset(int offset) { return _offset = offset; }
  };

  inline auto make_symbol(const std::string &name, std::shared_ptr<cdk::basic_type> type, int qualifier = 0) {
    return std::make_shared<symbol>(type, name, qualifier);
  }

} // til

#endif
