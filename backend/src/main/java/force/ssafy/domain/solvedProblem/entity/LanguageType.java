package force.ssafy.domain.solvedProblem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LanguageType {
    // 기존 언어들
    TYPESCRIPT("ts"),
    JAVASCRIPT("js"),
    PYTHON3("py3"),
    PYPY3("pypy3"),
    JAVA8("java8"),
    JAVA11("java11"),
    JAVA15("java15"),
    C("c"),
    C99("c99"),
    C90("c90"),
    C11("c11"),
    C2X("c2x"),
    CPP("cpp"),
    CPP98("cpp98"),
    CPP11("cpp11"),
    CPP14("cpp14"),
    CPP17("cpp17"),
    CPP20("cpp20"),
    CPP23("cpp23"),
    CPP26("cpp26"),
    CSHARP("cs"),
    GO("go"),
    GO_GCCGO("go_gccgo"),
    RUBY("rb"),
    RUST2015("rust2015"),
    RUST2018("rust2018"),
    RUST2021("rust2021"),
    SWIFT("swift"),
    PHP("php"),
    D("d"),
    D_LDC("d_ldc"),
    KOTLIN("kotlin"),
    PASCAL("pas"),
    LUA("lua"),
    PERL("pl"),
    FSHARP("fs"),
    VISUALBASIC("vb"),
    OBJECTIVEC("m"),
    OBJECTIVECPP("mm"),
    BASH("sh"),
    PLAINTEXT("txt"),

    // 추가된 언어들
    FORTRAN("f"),
    SCHEME("scm"),
    ADA("ada"),
    AWK("awk"),
    OCAML("ml"),
    BRAINFUCK("bf"),
    WHITESPACE("ws"),
    TCL("tcl"),
    RHINO("js"),
    PIKE("pike"),
    SED("sed"),
    INTERCAL("i"),
    BC("bc"),
    ALGOL68("a68"),
    BEFUNGE("bf"),
    FREEBASIC("bas"),
    HAXE("hx"),
    LOLCODE("lol"),
    AHEUI("aheui"),
    SYSTEMVERILOG("sv"),
    GOLFSCRIPT("gs"),
    ASSEMBLY32("asm32"),
    ASSEMBLY64("asm64");

    private final String type;
}