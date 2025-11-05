<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
const notify = useNotify();

</script>

<template>
    <div class="container-fluid mt-4 px-5">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body py-2 px-3">
                <div class="page-header d-flex align-items-center justify-content-between">
                    <div>
                        <h3 class="fw-bold text-warning mb-1">Thống kê</h3>
                        <Breadcrumb class="mt-2 mb-0" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>