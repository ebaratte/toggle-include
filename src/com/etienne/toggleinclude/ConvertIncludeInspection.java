package com.etienne.toggleinclude;


import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.jetbrains.cidr.lang.psi.OCFile;
import com.jetbrains.cidr.lang.psi.OCIncludeDirective;
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor;
import com.jetbrains.cidr.lang.util.OCElementFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ConvertIncludeInspection extends LocalInspectionTool {

    private final IncludeQuickFix quickFixAngles = new IncludeQuickFixAngles();
    private final IncludeQuickFix quickFixQuotes = new IncludeQuickFixQuotes();


    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "#include <> vs #include \"\"";
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "General";
    }

    @NotNull
    @Override
    public String[] getGroupPath() {
        return new String[]{"C/C++", "General"};
    }

    @NotNull
    @Override
    public String getShortName() {
        return "ConvertInclude";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new OCVisitor() {
            @Override
            public void visitImportDirective(OCIncludeDirective directive) {
                super.visitImportDirective(directive);
                String text = directive.getText();
                PsiFile file = directive.getIncludedFile();

                if (file instanceof OCFile) {
                    boolean inProject = (((OCFile) file).isInProjectSources());
                    if (ConvertUtils.quotes(text) && !inProject) {
                        holder.registerProblem(directive, "Use <> to include system/library headers", quickFixAngles);
                    } else if (ConvertUtils.angles(text) && inProject) {
                        holder.registerProblem(directive, "Use \"\" to include project headers", quickFixQuotes);
                    }
                }
            }
        };
    }

    private static abstract class IncludeQuickFix implements LocalQuickFix {

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            OCIncludeDirective directive = (OCIncludeDirective) descriptor.getPsiElement();
            String text = directive.getText();
            directive.replace(OCElementFactory.topLevelDeclarationFromText(getReplaceText(text), directive.getParent()));
        }

        protected abstract String getReplaceText(String text);
    }

    private class IncludeQuickFixAngles extends IncludeQuickFix {

        @Nls
        @NotNull
        @Override
        public String getName() {
            return "Convert to #include <>";
        }

        @Override
        protected String getReplaceText(String text) {
            return ConvertUtils.toAngles(text);
        }
    }

    private class IncludeQuickFixQuotes extends IncludeQuickFix {
        @Nls
        @NotNull
        @Override
        public String getName() {
            return "Convert to #include \"\"";
        }

        @Override
        protected String getReplaceText(String text) {
            return ConvertUtils.toQuotes(text);
        }
    }
}
